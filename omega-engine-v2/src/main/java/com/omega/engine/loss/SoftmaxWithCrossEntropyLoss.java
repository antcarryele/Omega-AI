package com.omega.engine.loss;

import com.omega.engine.nn.data.Blob;
import com.omega.engine.nn.data.Blobs;

/**
 * Cross Entropy loss function
 * 
 * @author Administrator
 *
 * @loss: - ∑ y * ln(f(x))
 * @diff: - ∑ y * (1 / f(x))
 *
 */
public class SoftmaxWithCrossEntropyLoss extends LossFunction {

	public final LossType lossType = LossType.cross_entropy;
	
	private static SoftmaxWithCrossEntropyLoss instance;
	
	private final double eta = 0.000000000001;
	
	public static SoftmaxWithCrossEntropyLoss operation() {
		if(instance == null) {
			instance = new SoftmaxWithCrossEntropyLoss();
		}
		return instance;
	}

	@Override
	public LossType getLossType() {
		// TODO Auto-generated method stub
		return LossType.cross_entropy;
	}

	@Override
	public Blob loss(Blob x, double[][] label) {
		// TODO Auto-generated method stub
		Blob temp = Blobs.blob(x.number,x.channel,x.height,x.width);
		
		for(int n = 0;n<x.number;n++) {
			for(int o = 0;o<x.width;o++) {
				if(x.maxtir[n][0][0][o] == 0.0d) {
					temp.maxtir[n][0][0][o] = - label[n][o] * Math.log(eta);
				}else {
					temp.maxtir[n][0][0][o] = - label[n][o] * Math.log(x.maxtir[n][0][0][o]);
				}
			}
		}

		return temp;
	}

	@Override
	public Blob diff(Blob x, double[][] label) {
		// TODO Auto-generated method stub
		Blob temp = Blobs.blob(x.number,x.channel,x.height,x.width);
		for(int n = 0;n<x.number;n++) {
			for(int o = 0;o<x.width;o++) {
				temp.maxtir[n][0][0][o] = - label[n][o] / x.maxtir[n][0][0][o];
			}
		}
		return temp;
	}
	
	public static void main(String[] args) {
		double[][] x = new double[][] {{0.2,0.3,0.5},{0.1,0.1,0.8},{0.3,0.1,0.6},{0.9,0.01,0.09}};
		Blob xb = Blobs.blob(4, 1, 1, 3, x);
		double[][] label = new double[][] {{0,1,0},{1,0,0},{1,0,0},{0,0,1}};
		double error = SoftmaxWithCrossEntropyLoss.operation().gradientCheck(xb,label);
		System.out.println("error:"+error);
	}

}
