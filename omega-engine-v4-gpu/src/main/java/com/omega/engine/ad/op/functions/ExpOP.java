package com.omega.engine.ad.op.functions;

import com.omega.common.data.Tensor;
import com.omega.engine.ad.Tape;
import com.omega.engine.ad.op.FunctionOP;
import com.omega.engine.ad.op.OPType;
import com.omega.engine.ad.op.TensorOP;

public class ExpOP extends FunctionOP {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3857343378511617891L;

	public static ExpOP op = null;
	
	public static final OPType opt = OPType.exp;
	
	public static ExpOP getInstance() {
		if(op == null) {
			op = new ExpOP();
			op.setOpType(opt);
		}
		return op;
	}
	
	@Override
	public Tensor forward(Tape tape) {
		// TODO Auto-generated method stub
		Tensor self = tape.getX();
		Tensor y = tape.getOutput();
		TensorOP.exp(self, y);
		if(self.isRequiresGrad()) {
			y.setRequiresGrad(true);
		}
		return y;
	}
	
	/**
	 * exp'(x) = exp(x)
	 */
	@Override
	public void backward(Tensor delta, Tape tape) {
		// TODO Auto-generated method stub
		Tensor x = tape.getX();
		if(x.isRequiresGrad()) {
			Tensor dy = tape.getOutput();
			TensorOP.mulPlus(delta, dy, x.getGrad());
		}
	}

}
