#define BLOCK 1024 
#define C10_WARP_SIZE 32

#include <cuda.h>

__device__ __forceinline__ void VectorizedAtomicAddPerBlock(
    const int64_t len, int tid, int threads_per_block, const float *in, float *out) {
  for (int i = tid; i < len; i += threads_per_block) {
    atomicAdd(&out[i], in[i]);
  }
}

extern "C"
__global__ void EmbeddingFW(float *output,
                            const float *table,
                            const float *ids,
                            const int N,
                            const int K,
                            const int D) {
  int idx = threadIdx.x;
  int idy = blockIdx.x + threadIdx.y * gridDim.x;

  while (idy < K) {
    auto id = static_cast<int64_t>(ids[idy]);
    
    float *out = output + idy * D;
    const float *tab = table + id * D;
    for (int i = idx; i < D; i += blockDim.x) {
      out[i] = tab[i];
    }
    idy += blockDim.y * gridDim.x;
  }
}

extern "C"
__global__ void EmbeddingGrad(float* table,
                              const float* output,
                              const float* ids,
                              const int N,
                              const int K,
                              const int D) {
  int idx = threadIdx.x;
  int idy = blockIdx.x + threadIdx.y * gridDim.x;

  while (idy < K) {
    auto id = static_cast<int>(ids[idy]);
    const float* out = output + idy * D;
    float* tab = table + id * D;
    //for (int i = idx; i < D; i += blockDim.x) {
      //atomicAdd(&tab[i], out[i]);
    //}
    VectorizedAtomicAddPerBlock(D, idx, blockDim.x, out, tab);
	idy += blockDim.y * gridDim.x;
  }
}

extern "C"
__global__ void embedding_backward_kernel(float* input, float* indices, float* grad_output, float* grad_weight,
  float* count, int64_t numel, int64_t stride, int padding_idx)
{
	  int idx = blockIdx.x * 4 + threadIdx.y;
	
	  // Each warp is responsible for an input into the LookupTable.
	  // If the preceding input has the same as this input, then the warp
	  // exits immediately. The warp also processes subsequent inputs with the
	  // same value.
	  //
	  // Input Warp
	  // 1     <warp 1>
	  // 1     <warp 1> (<warp 2> exits without doing any work)
	  // 5     <warp 3>
	  // 8     <warp 4>
	
	  // Number of values processed by each thread (grain size)
	  const int SZ = 4;
	
	  if (idx < numel
	      && (idx == 0 || input[idx] != input[idx - 1])
	      && input[idx] != padding_idx) {
	    do {
	      const int start_feature = threadIdx.x + blockIdx.y * blockDim.x * SZ;
	      const int weight_row = ((int) input[idx]) * stride;
	      const int grad_row = ((int) indices[idx]) * stride;
	      const float scale = count ? (float)1.0 / count[idx] : 1.0;
	
	      float gradient[SZ];
	      float weight[SZ];
	
	      #pragma unroll
	      for (int ii = 0; ii < SZ; ii++) {
	        int feature_dim = start_feature + ii * C10_WARP_SIZE;
	        if (feature_dim < stride) {
	          gradient[ii] = static_cast<float>(grad_output[grad_row + feature_dim]);
	          weight[ii] = static_cast<float>(grad_weight[weight_row + feature_dim]);
	        }
	      }
	
	      #pragma unroll
	      for (int ii = 0; ii < SZ; ii++) {
	        weight[ii] += gradient[ii] * scale;
	      }
	
	      #pragma unroll
	      for (int ii = 0; ii < SZ; ii++) {
	        int feature_dim = start_feature + ii * C10_WARP_SIZE;
	        if (feature_dim < stride) {
	            grad_weight[weight_row + feature_dim] = static_cast<float>(weight[ii]);
	        }
	      }
	
	      idx++;
	    } while (idx < numel && input[idx] == input[idx - 1]);
	  }
}
