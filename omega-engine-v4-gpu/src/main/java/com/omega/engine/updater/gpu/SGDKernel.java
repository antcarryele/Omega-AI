package com.omega.engine.updater.gpu;

import static jcuda.driver.JCudaDriver.cuLaunchKernel;

import com.omega.common.data.Tensor;
import com.omega.common.lib.LibPaths;
import com.omega.engine.gpu.CUDAModules;
import com.omega.engine.nn.network.Network;

import jcuda.Pointer;
import jcuda.driver.CUfunction;

public class SGDKernel {
	
	public Tensor vw;
	
	public Tensor vb;
	
	private float momentum = 0.9f;
	
//	private float dampening = 0f;
	
	public float weight_decay = 5e-4f;
	
	private CUfunction function;
	
	private CUfunction bn_function;
	
	private int CAFFE_CUDA_NUM_THREADS = 1024;
	
	private Pointer kernelParameters;
	
//	private Pointer kernelBiasParameters;
	
	public SGDKernel(int weightLength) {
		this.vw = new Tensor(1, 1, 1, weightLength, true);
		init();
	}
	
	public SGDKernel(int weightLength,int biasLength) {
		this.vw = new Tensor(1, 1, 1, weightLength, true);
		this.vb = new Tensor(1, 1, 1, biasLength, true);
		init();
	}
	
	public void init() {
		/**
		 * 初始化cuda函数
		 */
		initFunction();

	}
	
	public void initFunction() {
		
		try {

			if(function == null) {

				function = CUDAModules.getFunctionByModule(LibPaths.LIB_PATH+"updater.cu", "sgd");
				
			}
			
			if(bn_function == null) {
				
				bn_function = CUDAModules.getFunctionByModule(LibPaths.LIB_PATH+"updater.cu", "sgd_bn");
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public void updateW(Tensor diffW,Tensor weight,Network net,float lr) {
		
		try {
			
	        /**
	         * 设置入参
	         * float *diffW, float *v,float *weight,float momentum,float weight_decay,float learnRate, int n, int batch
	         */ 
			kernelParameters = Pointer.to(
					Pointer.to(diffW.getGpuData()),
	        		Pointer.to(vw.getGpuData()),
	                Pointer.to(weight.getGpuData()),
	                Pointer.to(new float[]{momentum}),
	                Pointer.to(new float[]{weight_decay}),
	                Pointer.to(new float[]{lr}),
	                Pointer.to(new int[]{diffW.dataLength}),
	                Pointer.to(new int[]{net.number}),
	                Pointer.to(new int[]{net.train_time})
	            );

			cuLaunchKernel(function,
		            this.CAFFE_GET_BLOCKS(diffW.dataLength),  1, 1,      // Grid dimension
		            CAFFE_CUDA_NUM_THREADS, 1, 1,      // Block dimension
		            0, null,               // Shared memory size and stream
		            kernelParameters, null // Kernel- and extra parameters
		        );

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public void updateB(Tensor diffB,Tensor bias,Network net,float lr) {
		
		try {
			
	        /**
	         * 设置入参
	         * float *diffW, float *v,float *weight,float momentum,float weight_decay,float learnRate, int n, int batch
	         */ 
			kernelParameters = Pointer.to(
					Pointer.to(diffB.getGpuData()),
	        		Pointer.to(vb.getGpuData()),
	                Pointer.to(bias.getGpuData()),
	                Pointer.to(new float[]{momentum}),
	                Pointer.to(new float[]{0.0f}),
	                Pointer.to(new float[]{lr}),
	                Pointer.to(new int[]{diffB.dataLength}),
	                Pointer.to(new int[]{net.number}),
	                Pointer.to(new int[]{net.train_time})
	            );

			cuLaunchKernel(function,
		            this.CAFFE_GET_BLOCKS(diffB.dataLength),  1, 1,      // Grid dimension
		            CAFFE_CUDA_NUM_THREADS, 1, 1,      // Block dimension
		            0, null,               // Shared memory size and stream
		            kernelParameters, null // Kernel- and extra parameters
		        );

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public int CAFFE_GET_BLOCKS(int N) {
	    return (N + CAFFE_CUDA_NUM_THREADS - 1) / CAFFE_CUDA_NUM_THREADS;
	}
	
	
	
}
