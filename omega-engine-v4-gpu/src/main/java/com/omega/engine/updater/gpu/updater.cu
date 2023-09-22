#define BLOCK 1024 
#define ETA 1e-8 

extern "C"
__global__ void adam(float *diffW, float *weight,float *mw,float *vw,float beta1,float beta2,float learnRate, int n, int batch, int t)
{
    int i = (blockIdx.x + blockIdx.y*gridDim.x) * blockDim.x + threadIdx.x;
    if (i >= n) return;
    float diff = diffW[i] / batch;
    mw[i] = beta1 * mw[i] + (1 - beta1) * diff;
	vw[i] = beta2 * vw[i] + (1 - beta2) * diff * diff;
	float mhat = mw[i] / (1 - powf(beta1, t));
	float vhat = vw[i] / (1 - powf(beta2, t));
	weight[i] = weight[i] - learnRate * mhat / (sqrt(vhat) + ETA);
}

extern "C"
__global__ void adamw(float *diffW, float *weight,float *mw,float *vw,float beta1,float beta2,float learnRate, float weight_decay, int n, int batch, int t)
{
    int i = (blockIdx.x + blockIdx.y*gridDim.x) * blockDim.x + threadIdx.x;
    if (i >= n) return;
    float diff = diffW[i] / batch;
    weight[i] = weight[i] * (1.0f - learnRate * weight_decay);
    mw[i] = beta1 * mw[i] + (1.0f - beta1) * diff;
	vw[i] = beta2 * vw[i] + (1.0f - beta2) * diff * diff;
	float mhat = mw[i] / (1.0f - powf(beta1, t));
	float vhat = vw[i] / (1.0f - powf(beta2, t));
	weight[i] = weight[i] - learnRate * mhat / (sqrt(vhat) + ETA);
}

extern "C"
__global__ void adamwr(float *diffW, float *weight,float *mw,float *vw,float beta1,float beta2,float learnRate, float weight_decay, int n, int batch, int t)
{
    int i = (blockIdx.x + blockIdx.y*gridDim.x) * blockDim.x + threadIdx.x;
    if (i >= n) return;
    mw[i] = beta1 * mw[i] + (1 - beta1) * diffW[i];
	vw[i] = beta2 * vw[i] + (1 - beta2) * diffW[i] * diffW[i];
	float mhat = mw[i] / (1 - powf(beta1, t));
	float vhat = vw[i] / (1 - powf(beta2, t));
	float lr_t = learnRate * sqrt(1 - powf(beta2, t)) / (1 - powf(beta1, t));
	weight[i] = weight[i] - lr_t  * (mhat / (sqrt(vhat) + ETA) - (batch * weight_decay * weight[i]));
}

extern "C"
__global__ void adam_bn(float *diffW, float *weight,float *mw,float *vw,float beta1,float beta2,float learnRate, int n, int batch, int t)
{
    int i = (blockIdx.x + blockIdx.y*gridDim.x) * blockDim.x + threadIdx.x;
    if (i >= n) return;
    float diff = diffW[i] / batch;
    mw[i] = beta1 * mw[i] + (1 - beta1) * diff;
	vw[i] = beta2 * vw[i] + (1 - beta2) * diff * diff;
	float mhat = mw[i] / (1 - powf(beta1, t));
	float vhat = vw[i] / (1 - powf(beta2, t));
	weight[i] = weight[i] - learnRate * mhat / (sqrt(vhat) + ETA);
}

extern "C"
__global__ void adamw_bn(float *diffW, float *weight,float *mw,float *vw,float beta1,float beta2,float learnRate, float weight_decay, int n, int batch, int t)
{
    int i = (blockIdx.x + blockIdx.y*gridDim.x) * blockDim.x + threadIdx.x;
    if (i >= n) return;
    float tmp = weight[i] - learnRate * weight_decay * weight[i];
    float diff = diffW[i] / batch;
    mw[i] = beta1 * mw[i] + (1 - beta1) * diff;
	vw[i] = beta2 * vw[i] + (1 - beta2) * diff * diff;
	float mhat = mw[i] / (1 - powf(beta1, t));
	float vhat = vw[i] / (1 - powf(beta2, t));
	weight[i] = tmp - learnRate * (mhat / (sqrt(vhat) + ETA));
}

extern "C"
__global__ void sgd(float *diffW, float *v,float *weight,float momentum,float weight_decay,float learnRate, int n, int batch, int t)
{
    int i = (blockIdx.x + blockIdx.y*gridDim.x) * blockDim.x + threadIdx.x;
    if (i >= n) return;
    float gt = diffW[i] / batch;
    gt = gt + weight_decay * weight[i];
    if(t > 1){
    	v[i] = momentum * v[i] + gt;
    }else{
    	v[i] = gt;
    }
    gt = gt + momentum * v[i];
	weight[i] = weight[i] - learnRate * gt;
}

extern "C"
__global__ void sgd_bn(float *diffW, float *v,float *weight,float momentum,float weight_decay,float learnRate, int n, int batch, int t)
{
    int i = (blockIdx.x + blockIdx.y*gridDim.x) * blockDim.x + threadIdx.x;
    if (i >= n) return;
    float gt = diffW[i] / batch;
    if(t > 1){
    	v[i] = momentum * v[i] + gt;
    }else{
    	v[i] = gt;
    }
    gt = gt + momentum * v[i];
	weight[i] = weight[i] - learnRate * gt;
}

extern "C"
__global__ void RMSProp(float *diffW, float *rw, float *weight, float mul, float eta, float learnRate, int n, int batch,int clamp,float min,float max)
{
    int i = (blockIdx.x + blockIdx.y*gridDim.x) * blockDim.x + threadIdx.x;
    if (i >= n) return;
    float gt = diffW[i] / batch;
    rw[i] = mul * rw[i] + (1 - mul) * gt * gt;
    gt = learnRate / sqrtf(rw[i] + eta) * gt;
	weight[i] = weight[i] - gt;
	if(clamp == 1){
		if(weight[i] < min){
			weight[i] = min;
		}
		if(weight[i] > max){
			weight[i] = max;
		}
	}
}