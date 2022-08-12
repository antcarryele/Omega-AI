package com.omega.engine.gpu;

import static jcuda.driver.JCudaDriver.cuLaunchKernel;
import static jcuda.jcublas.JCublas2.cublasSetVector;

import com.omega.common.utils.CheckArrayUtils;
import com.omega.common.utils.Im2colToVector;
import com.omega.common.utils.Im2colUtils;
import com.omega.common.utils.MatrixUtils;
import com.omega.common.utils.RandomUtils;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.CUfunction;
import jcuda.driver.JCudaDriver;
import jcuda.runtime.JCuda;

public class ConvWeightKernel {
	
	private String id;
	private float[] x;
	private float[] kernel;
//	private float[] x_im2col;
	private float[] out;
	private int C;
	private int H;
	private int W;
	private int ko;
	private int kh;
	private int kw;
	private int s;
	private int oHeight;
	private int oWidth;
	private int ih;
	private int iw;
	private CUfunction function;
	private int CAFFE_CUDA_NUM_THREADS = 1024;
	
	private CUdeviceptr dx;
	private CUdeviceptr dy;
	
	private Pointer dA;
	private Pointer dC;
	
	private Pointer kernelParameters;

	public ConvWeightKernel(String id,float[] out,int C,int H,int W,int ko,int kh,int kw,int s) {
		this.id = id;
		this.C = C;
		this.H = H;
		this.W = W;
		this.ko = ko;
		this.kh = kh;
		this.kw = kw;
		this.s = s;
		this.oHeight = ((H - kh ) / s) + 1;
		this.oWidth = ((W - kw) / s) + 1;
		this.out = out;
		this.ih = C * kh * kw;
		this.iw = oHeight * oWidth;

//		long start1 = System.nanoTime();
		
		init();
		
//        System.out.println((System.nanoTime() - start1) / 1e6 + "ms.1111");
	}
	
	public void initFunction() {
		
		try {

			if(function == null) {
				
				function = CUDAModules.getFunctionByModule("H://Im2colKernel.cu", "im2col_gpu_kernel");
        
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public void init() {
		/**
		 * 初始化cuda函数
		 */
		initFunction();
		
//		/**
//		 * 申请内存
//		 */
//		if(x_im2col == null) {
//			x_im2col = new float[ih * iw];
//		}

		/**
		 * 申请显存
		 */
		this.dx = CUDAMemoryManager.getDevice(id + "_dx", C * H * W);
		this.dy = CUDAMemoryManager.getDevice(id + "_dy", ih * iw);
		
        this.dA = CUDAMemoryManager.getPointer(id + "_dA", ko * ih);
        this.dC = CUDAMemoryManager.getPointer(id + "_dC", ko * iw);
		
        /**
         * 设置入参
         * float* data_im,float* data_col,int n,int height,int width,int kh,int kw,int s,int oh,int ow
         */
        int num_kernels = C * oHeight * oWidth; 
        
        kernelParameters = Pointer.to(
                Pointer.to(dx),
                Pointer.to(dy),
                Pointer.to(new int[]{num_kernels}),
                Pointer.to(new int[]{H}),
                Pointer.to(new int[]{W}),
                Pointer.to(new int[]{kh}),
                Pointer.to(new int[]{kw}),
                Pointer.to(new int[]{s}),
                Pointer.to(new int[]{oHeight}),
                Pointer.to(new int[]{oWidth})
            );
        
	}
	
	public void setX(float[] x) {
		this.x = x;
	}
	
	public void setKernel(float[] kernel) {
		this.kernel = kernel;
		cublasSetVector(ko * ih, Sizeof.FLOAT, Pointer.to(kernel), 1, dA, 1);
	}
	
	public int CAFFE_GET_BLOCKS(int N) {
	    return (N + CAFFE_CUDA_NUM_THREADS - 1) / CAFFE_CUDA_NUM_THREADS;
	}
	
	public void conv() {

//        long start = System.nanoTime();

		im2col();
		
		sgemm();

//        System.out.println((System.nanoTime() - start) / 1e6 + "ms22222");
        
	}
	
	public void sgemm() {
		
		GPUOP.getInstance().multiplyFloat(ko, ih, iw, getOut(), dA, dy, dC);
		
	}
	
	public void im2col() {
		
		try {
//			long start1 = System.nanoTime();

			/**
			 * 申请内存
			 */
	        JCudaDriver.cuMemcpyHtoD(dx, Pointer.to(x), x.length * Sizeof.FLOAT);

//	        long start3 = System.nanoTime();

	        cuLaunchKernel(function,
		            this.CAFFE_GET_BLOCKS(ih),  1, 1,      // Grid dimension
		            CAFFE_CUDA_NUM_THREADS, 1, 1,      // Block dimension
		            0, null,               // Shared memory size and stream
		            kernelParameters, null // Kernel- and extra parameters
		        );
	        
//	        JCudaDriver.cuCtxSynchronize();
//	        System.out.println((System.nanoTime() - start2) / 1e6 + "ms2");
	        
//	        long start4 = System.nanoTime();
//	        System.out.println(out.length);
//	        JCudaDriver.cuMemcpyDtoH(Pointer.to(x_im2col), dy, x_im2col.length * Sizeof.FLOAT);

	        // Clean up.
//	        JCuda.cudaFree(dx);
//	        JCuda.cudaFree(dy);
//	        System.out.println((System.nanoTime() - start1) / 1e6 + "ms1");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public void free() {
		 JCuda.cudaFree(dx);
	     JCuda.cudaFree(dy);
	     if(dA!=null) {
	    	 GPUOP.getInstance().free(dA);
		     GPUOP.getInstance().free(dC);
	     }
	}
	
    public static void main(String args[]){	
    	int N = 128;
    	int C = 512;
    	int H = 9;
    	int W = 9;
    	int ko = 512;
    	int kh = 3;
    	int kw = 3;
    	int s = 1;
    	int oHeight = ((H - kh) / s) + 1;
		int oWidth = ((W - kw) / s) + 1;
		int ow = oHeight * oWidth;
		int oh = ko;
    	
    	float[] x1 = RandomUtils.gaussianRandom(N * C * H * W, 0.1f);
    	
    	float[] k1 = RandomUtils.gaussianRandom(ko * C * kh * kw, 0.1f);
    	
    	float[] out = new float[oh * ow];

    	float[][][][] out2 = new float[N][ko][oHeight][oWidth];
    	
    	float[][][][] out3 = new float[N][ko][oHeight][oWidth];

    	float[] once = new float[C * H * W];
    	
		ConvWeightKernel ck = new ConvWeightKernel("conv1", out, C, H, W, ko, kh, kw, s);

		ck.setKernel(k1);

    	long start = System.nanoTime();
    	
		for(int c = 0;c<20;c++){

	    	long start3 = System.nanoTime();
	    	for(int n = 0;n<N;n++) {
//	    		long start2 = System.nanoTime();
	    		System.arraycopy(x1, n * C * H * W, once, 0, C * H * W);
	    		ck.setX(once);
	        	ck.conv();
//	        	System.arraycopy(ck.getOut(), 0, out2, i * oh * ow, oh * ow);
	        	MatrixUtils.col2im4d(ck.getOut(), out2, n, ko, oHeight, oWidth);
//	        	System.out.println((System.nanoTime() - start2) / 1e6 + "ms.:"+i);
	    	}

	    	System.out.println((System.nanoTime() - start3) / 1e6 + "ms================>c.:"+c);
	    	
		}
		
		System.out.println((System.nanoTime() - start) / 1e6 + "ms.");

    	int ow2 = C * kh * kw;
		int oh2 = N * oHeight * oWidth;
    	
    	float[] im2col = new float[oh2 * ow2];
    	
    	float[][][][] x2 = MatrixUtils.transform(x1, N, C, H, W);
    	
    	float[][][][] k2 = MatrixUtils.transform(k1, ko, C, kh, kw);

    	float[] ka = Im2colUtils.kernalToVector(k2, false);
    	
    	float[] kt = Im2colUtils.kernalToVector2(k2);
    	
    	System.out.println("k:"+CheckArrayUtils.check(k1, kt));
		
    	float[] out1 = new float[N * oh * ow];

    	long start2 = System.nanoTime();
    	
    	for(int i = 0;i<20;i++) {
    		long start1 = System.nanoTime();

        	Im2colToVector.im2col(x2, im2col, kh, kw, s);

        	float[] r = new float[N * oh * ow];
        	
        	int xm = N * oHeight * oWidth;
    		int xn = kh * kw * C;
        	
    		GPUOP.getInstance().multiplyFloat(xm, xn, ko, im2col, ka, r);

	    	System.out.println((System.nanoTime() - start1) / 1e6 + "ms.cpu:"+i);
	    	out1 = r;
    	}
    	
    	System.out.println((System.nanoTime() - start2) / 1e6 + "ms.cpu-count");
    	
    	MatrixUtils.col2imgV2(out1, out3, N, ko, oHeight, oWidth);
    	
    	System.err.println(N * oh * ow);
    	
    	System.out.println(CheckArrayUtils.check(out2, out3));

		CUDAMemoryManager.free();
    }

	public float[] getOut() {
		return out;
	}

}
