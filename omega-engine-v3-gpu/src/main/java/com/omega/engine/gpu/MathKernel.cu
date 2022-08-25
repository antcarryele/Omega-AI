#define BLOCK 1024 


extern "C"
__global__ void mean_cov(float* x,float* mean,int number,int channel,int height,int width,float scale)
{
    
    for (int index = blockIdx.x * blockDim.x + threadIdx.x; index < channel; index += blockDim.x * gridDim.x) {
		
		float val = 0;
		
		for(int n = 0;n<number;n++) {	
			for(int h = 0;h<height;h++) {
				for(int w = 0;w<width;w++) {
				
					val += x[n * channel * height * width + index * height * width + h * width + w];
	
				}
			}
		}	
		
		mean[index] = val * scale;
	}

}


extern "C"
__global__ void mean_full(float* x,float* mean,int number,int width,float scale)
{
    
    for (int index = blockIdx.x * blockDim.x + threadIdx.x; index < width; index += blockDim.x * gridDim.x) {
		
		float val = 0;
		
		for(int n = 0;n<number;n++) {	
				
			val += x[n * width + index];
	
		}	
		
		mean[index] = val * scale;
	}

}

extern "C"
__global__ void var_cov(float* x,float* mean,float* var,int number,int channel,int height,int width,float scale)
{
    
    for (int index = blockIdx.x * blockDim.x + threadIdx.x; index < channel; index += blockDim.x * gridDim.x) {
		
		float val = 0;
		
		float mean_val = mean[index];
		
		for(int n = 0;n<number;n++) {	
			for(int h = 0;h<height;h++) {
				for(int w = 0;w<width;w++) {
					
					float x_val = x[n * channel * height * width + index * height * width + h * width + w];
					
					val += (x_val - mean_val) * (x_val - mean_val);
	
				}
			}
		}	
		
		var[index] = val * scale;
	}

}

extern "C"
__global__ void var_full(float* x,float* mean,float* var,int number,int width,float scale)
{
    
    for (int index = blockIdx.x * blockDim.x + threadIdx.x; index < width; index += blockDim.x * gridDim.x) {
		
		float val = 0;
		
		float mean_val = mean[index];
		
		for(int n = 0;n<number;n++) {	
				
			float x_val = x[n * width + index];
				
			val += (x_val - mean_val) * (x_val - mean_val);
	
		}	
		
		var[index] = val * scale;
	}

}


extern "C"
__global__ void std_fn(float* var,float* std,float eta,int n)
{
    
    for (int index = blockIdx.x * blockDim.x + threadIdx.x; index < n; index += blockDim.x * gridDim.x) {
		
		std[index] = sqrt(var[index] + eta);
		
	}

}


extern "C"
__global__ void mwa(float* mean,float* std,float* runingMean,float* runingStd,int n)
{
    
    float alpha = 0.1;
    
    for (int index = blockIdx.x * blockDim.x + threadIdx.x; index < n; index += blockDim.x * gridDim.x) {
		
		runingMean[index] = alpha * runingMean[index] + (1 - alpha) * mean[index];
		
		runingStd[index] = alpha * runingStd[index] + (1 - alpha) * std[index];
		
	}

}


extern "C"
__global__ void  fast_mean_kernel(float *x, int batch, int filters, int spatial, float *mean)
{
    const int threads = BLOCK;
    __shared__ float local[threads];

    int id = threadIdx.x;
    local[id] = 0;

    int filter = blockIdx.x;

    int i, j;
    for(j = 0; j < batch; ++j){
        for(i = 0; i < spatial; i += threads){
            int index = j*spatial*filters + filter*spatial + i + id;
            local[id] += (i+id < spatial) ? x[index] : 0;
        }
    }

    __syncthreads();

    if(id == 0){
        mean[filter] = 0;
        for(i = 0; i < threads; ++i){
            mean[filter] += local[i];
        }
        mean[filter] /= spatial * batch;
    }
}


extern "C"
__global__ void  fast_variance_kernel(float *x, float *mean, int batch, int filters, int spatial, float *variance)
{
    const int threads = BLOCK;
    __shared__ float local[threads];

    int id = threadIdx.x;
    local[id] = 0;

    int filter = blockIdx.x;

    int i, j;
    for(j = 0; j < batch; ++j){
        for(i = 0; i < spatial; i += threads){
            int index = j*spatial*filters + filter*spatial + i + id;

            local[id] += (i+id < spatial) ? powf((x[index] - mean[filter]), 2) : 0;
        }
    }

    __syncthreads();

    if(id == 0){
        variance[filter] = 0;
        for(i = 0; i < threads; ++i){
            variance[filter] += local[i];
        }
        variance[filter] /= (spatial * batch - 1);
    }
}