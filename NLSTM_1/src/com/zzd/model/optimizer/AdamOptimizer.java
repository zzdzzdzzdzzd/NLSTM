package com.zzd.model.optimizer;

import com.zzd.model.utils.Matrix;

public class AdamOptimizer {
	
	double betal1=0.9;
	double betal2=0.9999;
	Matrix eps;
	int  t=0;
	Matrix m,v,mt,vt;
	
    public AdamOptimizer(Matrix weight) {
        int rows = weight.getRowDimension();
        int cols = weight.getColumnDimension();
        this.m = new Matrix(rows, cols);
        this.v = new Matrix(rows, cols);
        this.eps = new Matrix(rows, cols, 0.00000001);
    }
    
    public AdamOptimizer(){
    	
    }
    
    /**
     * Adam algorithm
     * @param dw
     * @return
     */
    public Matrix update(Matrix dw){
    	Matrix newDw;
    	
    	t++;
    	
    	// beta1*m+(1-beta1)*grad
    	this.m=m.times(betal1).plus(dw.times(1-betal1));
    	this.v=v.times(betal2).plus(dw.arrayTimes(dw).times(1-betal2));
    	
    	this.mt=m.times(1/(1-Math.pow(betal1, t)));
    	this.vt=v.times(1/(1-Math.pow(betal2, t)));
    	
    	newDw=mt.arrayRightDivide(vt.sqrt().plus(eps));
    	return newDw;
    }
    
    public AdamOptimizer clone(){
    	AdamOptimizer newAdam=new AdamOptimizer();
    	newAdam.betal1=this.betal1;
    	newAdam.betal2=this.betal2;
    	newAdam.eps=this.eps;
    	newAdam.m=this.m.copy();
    	newAdam.mt=this.mt.copy();
    	newAdam.t=this.t;
    	newAdam.v=this.v.copy();
    	newAdam.vt=this.vt.copy();
    	
    	return newAdam;
    }
	

}
