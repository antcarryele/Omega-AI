package com.omega.engine.optimizer;

import com.omega.common.utils.MathUtils;
import com.omega.common.utils.MatrixOperation;
import com.omega.engine.nn.data.BaseData;
import com.omega.engine.nn.data.Blob;
import com.omega.engine.nn.data.Blobs;
import com.omega.engine.nn.network.Network;
import com.omega.engine.optimizer.lr.LearnRateUpdate;

/**
 * 
 * Mini Batch Stochastic Gradient Descent
 * 
 * @author Administrator
 *
 */
public class MBSGDOptimizer extends Optimizer {
	
	public MBSGDOptimizer(Network network, int trainTime, double error,int batchSize) throws Exception {
		super(network, batchSize, trainTime, error);
		// TODO Auto-generated constructor stub
		this.batchSize = batchSize;
		this.loss = Blobs.blob(batchSize, this.network.oChannel, this.network.oHeight, this.network.oWidth);
		this.lossDiff = Blobs.blob(batchSize, this.network.oChannel, this.network.oHeight, this.network.oWidth);
	}

	public MBSGDOptimizer(Network network, int trainTime, double error,int batchSize,LearnRateUpdate learnRateUpdate) throws Exception {
		super(network, batchSize, trainTime, error);
		// TODO Auto-generated constructor stub
		this.batchSize = batchSize;
		this.loss = Blobs.blob(batchSize, this.network.oChannel, this.network.oHeight, this.network.oWidth);
		this.lossDiff = Blobs.blob(batchSize, this.network.oChannel, this.network.oHeight, this.network.oWidth);
		this.learnRateUpdate = learnRateUpdate;
	}
	
	@Override
	public void train(BaseData trainingData) {
		// TODO Auto-generated method stub

		try {
			
			for(int i = 0;i<this.trainTime;i++) {
				
				this.trainIndex = i;
				
				if(this.currentError <= this.error && this.trainIndex >= this.minTrainTime) {
					break;
				}
				
				this.loss.clear();
				
				this.lossDiff.clear();

				/**
				 * random data index
				 */
				int[] dataSetIndexs = MathUtils.randomInt(trainingData.number - 1, this.batchSize);

				Blob input = trainingData.getRandomData(dataSetIndexs); 
				
				/**
				 * forward
				 */
				Blob output = this.network.forward(input);
				
				/**
				 * loss
				 */
				this.loss = this.network.loss(output, input.labels);
				
				/**
				 * loss diff
				 */
				this.lossDiff = this.network.lossDiff(output, input.labels);
				
				/**
				 * current time error
				 */
				this.currentError = MatrixOperation.sum(this.loss.maxtir) / this.batchSize;
				
				/**
				 * update learning rate
				 */
				this.updateLR();
				
				/**
				 * back
				 */
				this.network.back(this.lossDiff);
				
				System.out.println("training["+this.trainIndex+"] (lr:"+this.network.learnRate+") currentError:"+this.currentError);
				
			}
			
			/**
			 * 停止训练
			 */
			System.out.println("training finish. ["+this.trainIndex+"] finalError:"+this.currentError);
//			System.out.println(JsonUtils.toJson(this.network.layerList));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
