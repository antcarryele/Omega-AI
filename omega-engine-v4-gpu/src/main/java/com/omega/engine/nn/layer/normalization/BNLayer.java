package com.omega.engine.nn.layer.normalization;

import com.omega.common.data.Tensor;
import com.omega.common.utils.MatrixUtils;
import com.omega.engine.gpu.cudnn.BNCudnnKernel;
import com.omega.engine.nn.layer.Layer;
import com.omega.engine.nn.layer.LayerType;
import com.omega.engine.nn.layer.gpu.BNBaseKernel;
import com.omega.engine.nn.layer.normalization.gpu.BNKernel3;
import com.omega.engine.nn.model.LayerInit;
import com.omega.engine.nn.network.Network;

/**
 * 
 * Batch Normalization Layer
 * 
 * @author Administrator
 * 
 * mean = ∑x / m
 * std = (∑(x - mean)^2 / m)^1/2
 * zi = (xi - mean) / std
 * yi = gama * zi + beta
 */
public class BNLayer extends NormalizationLayer {
	
	public BNType bnType = null;

	/**
	 * if prelayer is conv layer meanNum = channel
	 * else if prelayer is fully layer meanNum = channel * height * width
	 */
	private int meanNum = 0;
	
	public Tensor gamma;
	
	public Tensor beta;
	
	public Tensor diffGamma;
	
	public Tensor diffBeta;
	
	private BNBaseKernel kernel;
	
//	private BNCudnnKernel kernel;

	
	public boolean hasRuning = true;
	
	public BNLayer() {
//		initParam();
		this.hasParams = true;
	}
	
	public BNLayer(Layer preLayer) {
		this.width = preLayer.width;
		this.height = preLayer.height;
		this.oWidth = preLayer.oWidth;
		this.oHeight = preLayer.oHeight;
		this.channel = preLayer.channel;
		this.oChannel = preLayer.oChannel;
		this.hasParams = true;
	}
	
	public BNLayer(Network network) {
		this.network = network;
	}
	
	@Override
	public void init() {

		this.number = this.network.number;
		
		if(preLayer == null) {
			preLayer = this.network.getPreLayer(this.index);
			this.channel = preLayer.oChannel;
			this.height = preLayer.oHeight;
			this.width = preLayer.oWidth;
			this.oChannel = this.channel;
			this.oHeight = this.height;
			this.oWidth = this.width;
		}

		if(this.bnType == null) {
			if(this.preLayer.getLayerType() == LayerType.conv) {
				this.setBnType(BNType.conv_bn);
				this.meanNum = this.channel;
			}else if(this.preLayer.getLayerType() == LayerType.full){
				this.setBnType(BNType.fully_bn);
				this.meanNum = this.channel * this.height * this.width;
			}
		}
		
		if(this.gamma == null || this.beta == null) {
			this.gamma = new Tensor(1, 1, 1, meanNum, MatrixUtils.one(this.meanNum), true);
			this.beta = new Tensor(1, 1, 1, meanNum, true);
			this.diffGamma = new Tensor(1, 1, 1, meanNum, true);
			this.diffBeta = new Tensor(1, 1, 1, meanNum, true);
		}

		if(this.output == null || this.number != this.output.number) {
//			this.diff = new Tensor(number, channel, height, width, true);
//			this.diff = this.network.getNextLayer(this.index).diff;
			this.output = new Tensor(number, oChannel, oHeight, oWidth, true);
//			this.output2 = new Tensor(number, oChannel, oHeight, oWidth);
		}
		
		if(kernel == null) {
			
			if(this.network.CUDNN) {
				kernel = new BNCudnnKernel(this.getBnType(), channel, height, width);
			}else {
//				kernel = new BNKernel(this.getBnType(), channel, height, width);
				if(this.getBnType() == BNType.fully_bn) {
					kernel = new BNKernel3(width, 1, 1);
				}else {
					kernel = new BNKernel3(channel, height, width);
				}
			}
		}
		
	}
	
	@Override
	public void initParam() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void initBack() {
		if(this.diff == null) {
			this.diff = this.network.getNextLayer(this.index).diff;
		}
	}
	
	public void initBack(Tensor diff) {
		this.diff = diff;
	}

	@Override
	public void output() {
		// TODO Auto-generated method stub
//		System.out.println(this.index+":"+input.number+":"+input.channel+":"+input.height+":"+input.width);
		kernel.forward(this.network.RUN_MODEL, gamma, beta, input, output);
//		
//		System.out.println("bn-output:");
//		output.showDM();

	}
	
	@Override
	public Tensor getOutput() {
		// TODO Auto-generated method stub
		return this.output;
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

	/**
	 * 原论文公式
	 * deltaGama = ∑ deta * z
	 * deltaBeta = ∑ deta
	 * dxhat = deta * gama
	 * dvar = ∑ dxhat * (xi - mean) * -1/2 * (var + eta)^-3/2
	 * dmean = ∑ dxhat * -1 / (var + eta)^1/2 + dvar * (∑ -2 * (x - mean)) / n
	 * dx = dxhat * 1 / (var + eta)^1/2 + dvar * 2(x - mean) / n + dmean * 1/n
	 * darknet公式
	 * dmean = (∑ dxhat * -1 / (var + eta)^1/2)
	 */
	@Override
	public void diff() {
		
//		long start = System.nanoTime();
//		System.out.println(index);
		kernel.backward(input, delta, diff, gamma, diffGamma, diffBeta);

//		System.out.println((System.nanoTime() - start) / 1e6 + "ms.");
		
	}
	
	@Override
	public void back() {
		// TODO Auto-generated method stub
//		System.out.println(bnType);
		this.initBack();
		/**
		 * 设置梯度
		 */
		this.setDelta();
		/**
		 * 计算梯度
		 */
		this.diff();
		
//		this.diff_caffe();
		
		if(this.network.GRADIENT_CHECK) {
			this.gradientCheck();
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
		if(this.updater != null){
			this.updater.updateForBN(this);
		}else{
			for(int i = 0;i<this.gamma.dataLength;i++) {
				this.gamma.data[i] -= this.learnRate * this.diffGamma.data[i];
			}
			for(int i = 0;i<this.beta.dataLength;i++) {
				this.beta.data[i] -= this.learnRate * this.diffBeta.data[i];
			}
		}
		
	}

	@Override
	public void showDiff() {
		// TODO Auto-generated method stub

	}

	@Override
	public LayerType getLayerType() {
		// TODO Auto-generated method stub
		return LayerType.bn;
	}

	@Override
	public LayerInit save() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[][][][] output(float[][][][] input) {
		// TODO Auto-generated method stub
		
		return null;
	}
	
	public BNType getBnType() {
		return bnType;
	}

	public void setBnType(BNType bnType) {
		this.bnType = bnType;
	}

	@Override
	public void initCache() {
		// TODO Auto-generated method stub
		
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
	

	
}
