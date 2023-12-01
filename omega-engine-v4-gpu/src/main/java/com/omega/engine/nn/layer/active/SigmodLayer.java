package com.omega.engine.nn.layer.active;

import java.util.Vector;

import com.omega.common.data.Tensor;
import com.omega.common.task.Task;
import com.omega.common.task.TaskEngine;
import com.omega.engine.nn.layer.Layer;
import com.omega.engine.nn.layer.LayerType;
import com.omega.engine.nn.layer.active.gpu.SigmodKernel;
import com.omega.engine.nn.network.Network;

/**
 * Sigmod active function Layer
 * @author Administrator
 *
 */
public class SigmodLayer extends ActiveFunctionLayer {
	
	private SigmodKernel kernel;
	
	public SigmodLayer() {

	}
	
	public SigmodLayer(Layer preLayer) {
		this.setPreLayer(preLayer);
	}
	
	public SigmodLayer(Network network) {
		this.network = network;
	}
	
	@Override
	public void initParam() {
		// TODO Auto-generated method stub
		
	}

	public void init() {
		super.init();
		if(kernel == null) {
			kernel = new SigmodKernel();
		}
	}
	
	@Override
	public void output() {
		// TODO Auto-generated method stub
//		input.showDM();
		kernel.forward(input, output);
	}

	@Override
	public Tensor getOutput() {
		// TODO Auto-generated method stub
		return this.output;
	}

	@Override
	public void diff() {
		// TODO Auto-generated method stub
		kernel.backward(output, delta, diff);
	}
	
	public void diffTemp() {
		// TODO Auto-generated method stub
		kernel.backwardTemp(output, delta, diff);
	}

	@Override
	public void forward() {
		// TODO Auto-generated method stub
		/**
		 * 参数初始化
		 */
		this.init();
		/**
		 * 设置输入
		 */
		this.setInput();
		/**
		 * 计算输出
		 */
		this.output();

	}

	@Override
	public void back() {
		// TODO Auto-generated method stub
		this.initBack();
		/**
		 * 设置梯度
		 */
		this.setDelta();
		/**
		 * 计算梯度
		 */
		this.diff();
		if(this.network.GRADIENT_CHECK) {
			this.gradientCheck();
		}
	}
	
	public void backTemp() {
		// TODO Auto-generated method stub
		this.initBack();
		/**
		 * 设置梯度
		 */
		this.setDelta();
		/**
		 * 计算梯度
		 */
		this.diffTemp();
		if(this.network.GRADIENT_CHECK) {
			this.gradientCheck();
		}
	}

	@Override
	public void showDiff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LayerType getLayerType() {
		// TODO Auto-generated method stub
		return LayerType.sigmod;
	}

	@Override
	public float[][][][] output(float[][][][] input) {
		// TODO Auto-generated method stub
		float[][][][] output = new float[this.number][this.oChannel][this.oHeight][this.oWidth];
		
		Vector<Task<Object>> workers = new Vector<Task<Object>>();
		
		for(int n = 0;n<this.number;n++) {
			final int index = n;
			workers.add(new Task<Object>(index) {
				@Override
			    public Object call() throws Exception {
					for(int c = 0;c<channel;c++) {
						for(int h = 0;h<height;h++) {
							for(int w = 0;w<width;w++) {
								output[index][c][h][w] = (float) (1f / (1f + Math.exp(-input[index][c][h][w])));
							}
						}
					}
					return null;
				}
			});
		}
		
		TaskEngine.getInstance(this.network.getThreadNum()).dispatchTask(workers);
		
		return output;
	}

	@Override
	public void initCache() {
		// TODO Auto-generated method stub
		
	}

	public void initBack(Tensor diff) {
		this.diff = diff;
	}

	@Override
	public void forward(Tensor inpnut) {
		// TODO Auto-generated method stub
		/**
		 * 参数初始化
		 */
		this.init();
		/**
		 * 设置输入
		 */
		this.setInput(inpnut);
		/**
		 * 计算输出
		 */
		this.output();
	}

	@Override
	public void back(Tensor delta) {
		// TODO Auto-generated method stub
		this.initBack(delta);
		/**
		 * 设置梯度
		 */
		this.setDelta(delta);
		/**
		 * 计算梯度
		 */
		this.diff();
		if(this.network.GRADIENT_CHECK) {
			this.gradientCheck();
		}
	}
	
	@Override
	public void forward(Tensor input,int batch, int step) {
		// TODO Auto-generated method stub
		/**
		 * 参数初始化
		 */
		this.init();
		/**
		 * 设置输入
		 */
		this.setInput(input);
		/**
		 * 计算输出
		 */
		this.output(batch, step);
	}

	@Override
	public void back(Tensor delta, int batch, int step) {
		// TODO Auto-generated method stub
		this.initBack(delta);
		/**
		 * 设置梯度
		 */
		this.setDelta(delta);
		/**
		 * 计算梯度
		 */
		this.diff(batch, step);
		if(this.network.GRADIENT_CHECK) {
			this.gradientCheck();
		}
	}

	@Override
	public void output(int batch,int step) {
		// TODO Auto-generated method stub
		kernel.forward(input, output, step * batch * input.getOnceSize(), batch * input.getOnceSize());
	}

	@Override
	public void diff(int batch,int step) {
		// TODO Auto-generated method stub
		kernel.backward(input, delta, diff, step * batch * diff.getOnceSize(), batch * diff.getOnceSize());
	}
	
}
