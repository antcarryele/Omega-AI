package com.omega.engine.ad.op.functions;

import com.omega.common.data.Tensor;
import com.omega.engine.ad.Tape;
import com.omega.engine.ad.op.FunctionOP;
import com.omega.engine.ad.op.OPType;
import com.omega.engine.ad.op.TensorOP;

public class LogOP extends FunctionOP{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3655207300898534573L;

	public static LogOP op = null;
	
	public static final OPType opt = OPType.log;
	
	public static LogOP getInstance() {
		if(op == null) {
			op = new LogOP();
			op.setOpType(opt);
		}
		return op;
	}
	
	@Override
	public Tensor forward(Tape tape) {
		// TODO Auto-generated method stub
		Tensor self = tape.getX();
		Tensor y = tape.getOutput();
		TensorOP.log(self, y);
		if(self.isRequiresGrad()) {
			y.setRequiresGrad(true);
		}
		return y;
	}

	@Override
	public void backward(Tensor delta, Tape tape) {
		// TODO Auto-generated method stub
		Tensor x = tape.getX();
		if(x.isRequiresGrad()) {
			Tensor dy = tape.getTmp();
			TensorOP.div(1.0f, x, dy);
			TensorOP.mulPlus(delta, dy, x.getGrad());
		}
	}

}
