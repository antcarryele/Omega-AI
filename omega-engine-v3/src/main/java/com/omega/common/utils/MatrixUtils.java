package com.omega.common.utils;

import java.util.Vector;

import com.omega.common.task.ForkJobEngine;
import com.omega.common.task.Task;
import com.omega.common.task.TaskEngine;

/**
 * MatrixUtils
 * @author Administrator
 *
 */
public class MatrixUtils {
	
	private static final int threadNum = 8;
	
	/**
	 * 
	 * @Title: zero
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[] zero(int size) {
		return new float[size];
	}
	
	/**
	 * 
	 * @Title: zero
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[] one(int size) {
		float[] temp = new float[size];
		for(int i = 0;i<size;i++) {
			temp[i] = 1.0f;
		}
		return temp;
	}
	
	/**
	 * 
	 * @Title: zero
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[] val(int size,float v) {
		float[] temp = new float[size];
		for(int i = 0;i<size;i++) {
			temp[i] = v;
		}
		return temp;
	}
	
	/**
	 * 
	 * @Title: zero
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[][] createMatrix(int heigth,int width,float value) {
		float[][] temp = new float[heigth][width];
		for(int h = 0;h<heigth;h++) {
			for(int w = 0;w<width;w++) {
				temp[h][w] = value;
			}
		}
		return temp;
	}
	
	/**
	 * 
	 * @Title: zero
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[][][] createMatrix(int channel,int heigth,int width,float value) {
		float[][][] temp = new float[channel][heigth][width];
		for(int c = 0;c<channel;c++) {
			for(int h = 0;h<heigth;h++) {
				for(int w = 0;w<width;w++) {
					temp[c][h][w] = value;
				}
			}
		}
		return temp;
	}
	
	/**
	 * 
	 * @Title: zero
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[][][] createMatrixByIndex(int channel,int heigth,int width) {
		float[][][] temp = new float[channel][heigth][width];
		
		for(int c = 0;c<channel;c++) {
			float index = 1;
			for(int h = 0;h<heigth;h++) {
				for(int w = 0;w<width;w++) {
					temp[c][h][w] = index;
					index++;
				}
			}
		}
		return temp;
	}
	
	/**
	 * 
	 * @Title: zero
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[][][][] createMatrixByIndex(int number,int channel,int heigth,int width) {
		float[][][][] temp = new float[number][channel][heigth][width];
		for(int n = 0;n<number;n++) {
			for(int c = 0;c<channel;c++) {
				float index = 1;
				for(int h = 0;h<heigth;h++) {
					for(int w = 0;w<width;w++) {
						temp[n][c][h][w] = index;
						index++;
					}
				}
			}
		}
		return temp;
	}
	
	/**
	 * 
	 * @Title: zero
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[][] zero(int x,int y) {
		return new float[x][y];
	}
	
	/**
	 * 
	 * @Title: zero
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[][][] zero(int x,int y,int z) {
		return new float[x][y][z];
	}
	
	/**
	 * 
	 * @Title: zero
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[][][][] zero(int x,int y,int z,int n) {
		return new float[x][y][z][n];
	}
	
	/**
	 * 
	 * @Title: zero
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[][][][][] zero(int x,int y,int z,int n,int m) {
		return new float[x][y][z][n][m];
	}
	
	/**
	 * 
	 * @Title: val for matrix
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[][][][] val(int x,int y,int z,int n, float val) {
		float[][][][] tmp = new float[x][y][z][n];
		
		for(int c = 0;c<tmp.length;c++) {
			for(int i = 0;i<tmp[c].length;i++) {
				for(int j = 0;j<tmp[c][i].length;j++) {
					for(int l = 0;l<tmp[c][i][j].length;l++) {
						tmp[c][i][j][l] = val;
					}
				}
			}
		}
		
		return tmp;
	}
	
	/**
	 * 
	 * @Title: val for matrix
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[][][][] val(int x,int y,int z,int n, float p,float val) {
		float[][][][] tmp = new float[x][y][z][n];
		
		for(int c = 0;c<tmp.length;c++) {
			for(int i = 0;i<tmp[c].length;i++) {
				for(int j = 0;j<tmp[c][i].length;j++) {
					for(int l = 0;l<tmp[c][i][j].length;l++) {
						if(RandomUtils.getInstance().nextFloat() > p) {
							tmp[c][i][j][l] = val;
						}
					}
				}
			}
		}
		
		return tmp;
	}
	
	/**
	 * 
	 * @Title: zero
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[] clear(float[] x) {
		if(x != null) {
			x = MatrixUtils.zero(x.length);
		}
		return x;
	}
	
	/**
	 * 
	 * @Title: zero
	 *
	 * @param size
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[][] clear(float[][] x) {
		if(x != null) {
			x = MatrixUtils.zero(x.length,x[0].length);
		}
		return x;
	}
	
	/**
	 * size
	 * @param data
	 * @return
	 */
	public static int[] size(float[][][][] data) {
		System.out.println("["+data.length+","+data[0].length+","+data[0][0].length+","+data[0][0][0].length+"]");
		return new int[]{data.length,data[0].length,data[0][0].length,data[0][0][0].length};
	}

	/**
	 * size
	 * @param data
	 * @return
	 */
	public static int[] size(float[][] data) {
		System.out.println("["+data.length+","+data[0].length);
		return new int[]{data.length,data[0].length};
	}
	

	/**
	 * 
	 * @Title: clone
	 *
	 * @param x
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[] clone(float[] x) {
		float[] temp = new float[x.length];
		for(int i = 0;i<x.length;i++) {
			temp[i] = x[i];
		}
		return temp;
	}
	
	/**
	 * 
	 * @Title: clone
	 *
	 * @param x
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[][] clone(float[][] x) {
		float[][] temp = new float[x.length][x[0].length];
		for(int i = 0;i<x.length;i++) {
			for(int j = 0;j<x[i].length;j++) {
				temp[i][j] = x[i][j];
			}
		}
		return temp;
	}
	
	/**
	 * 
	 * @Title: clone
	 *
	 * @param x
	 * @return
	 *
	 * @Description:
	 * TODO(这里用一句话描述这个方法的作用)
	 *
	 * @throws
	 */
	public static float[][][] clone(float[][][] x) {
		float[][][] temp = new float[x.length][x[0].length][x[0][0].length];
		for(int c = 0;c<x.length;c++) {
			for(int i = 0;i<x[c].length;i++) {
				for(int j = 0;j<x[c][i].length;j++) {
					temp[c][i][j] = x[c][i][j];
				}
			}
		}
		return temp;
	}
	
	/**
	 * transform
	 * @param x  c * h * w
	 * @index ci * h * w + hi * w + wi
	 * @return
	 */
	public static float[] transform(float[][][] x) {
		
		float[] result = new float[x.length * x[0].length * x[0][0].length];
		
		for(int c = 0;c<x.length;c++) {
			
			for(int h = 0;h<x[c].length;h++) {
				
				for(int w = 0;w<x[c][h].length;w++) {
					result[c*x[c].length*x[c][h].length + h*x[c][h].length + w] = x[c][h][w];
				}
			}
			
		}
		
		return result;
	}
	
	/**
	 * transform
	 * @param x  c * h * w
	 * @index ci * h * w + hi * w + wi
	 * @return
	 */
	public static float[] transform(float[][] x) {
		
		int xi = x.length;
		int xj = x[0].length;
		
		float[] result = new float[x.length * x[0].length];
		
		for(int r = 0;r<xi;r++) {
			
			for(int c = 0;c<xj;c++) {
				
				result[r * xj + c] = x[r][c];
				
			}
			
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param x
	 * @index ni * c * h * w + ci * h * w + hi * w + wi
	 * @return
	 */
	public static float[] transform(float[][][][] x){
		
		float[] y = new float[x.length * x[0].length * x[0][0].length * x[0][0][0].length];
		
		for(int n = 0;n<x.length;n++) {

			for(int c = 0;c<x[n].length;c++) {
				for(int h = 0;h<x[n][c].length;h++) {
					for(int w = 0;w<x[n][c][h].length;w++) {
						y[n*x[n].length*x[n][c].length*x[n][c][h].length + c*x[n][c].length*x[n][c][h].length + h*x[n][c][h].length + w] = x[n][c][h][w];
					}
				}
				
			}
		}
		return y;
	}
	
	/**
	 * transform
	 * @param x
	 * @return
	 */
	public static float[][] transform(float[] x,int r,int c) {
		
		float[][] y = new float[r][c];
		
		for(int ri = 0;ri<r;ri++) {
			for(int ci = 0;ci<c;ci++) {
				y[ri][ci] = x[ri * c + ci];
			}
		}
		
		return y;
	}
	
	/**
	 * transform
	 * @param x
	 * @return
	 */
	public static float[][][] transform(float[] x,int c,int h,int w) {
		
		float[][][] y = new float[c][h][w];
		
		for(int ci = 0;ci<c;ci++) {
			for(int hi = 0;hi<h;hi++) {
				for(int wi = 0;wi<w;wi++) {
					y[ci][hi][wi] = x[ci * h * w + hi * w + wi];
				}
			}
		}
		
		return y;
	}
	
	/**
	 * transform
	 * @param x
	 * @index ni * c * h * w + ci * h * w + hi * w + wi
	 * @return
	 */
	public static float[][][][] transform(float[] x,int n,int c,int h,int w) {
		
		float[][][][] y = new float[n][c][h][w];
		for(int ni = 0;ni<n;ni++) {
			for(int ci = 0;ci<c;ci++) {
				for(int hi = 0;hi<h;hi++) {
					for(int wi = 0;wi<w;wi++) {
						y[ni][ci][hi][wi] = x[ni * c * h * w + ci * h * w + hi * w + wi];
					}
				}
			}
		}
		
		return y;
	}
	
	/**
	 * transform
	 * @param x
	 * @return
	 */
	public static float[][][][] transform(float[][] x,int n,int c,int h,int w) {
		
		float[][][][] y = new float[n][c][h][w];
		
		for(int ni = 0;ni<n;ni++) {
			for(int ci = 0;ci<c;ci++) {
				for(int hi = 0;hi<h;hi++) {
					for(int wi = 0;wi<w;wi++) {
						y[ni][ci][hi][wi] = x[ni][ci * h * w + hi * w + wi];
					}
				}
			}
		}

		return y;
	}
	
	/**
	 * transform
	 * @param x  c * h * w
	 * @return
	 */
	public static float[][][] transform(float[][] x,int oh,int ow) {
		
		int channel = x[0].length;
		
		float[][][] result = new float[channel][oh][ow];
		
		for(int c = 0;c<channel;c++) {
			int index = 0;
			for(int h = 0;h<oh;h++) {
				for(int w = 0;w<ow;w++) {
					result[c][h][w] = x[index][c];
					index++;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * transform
	 * @param x
	 * @return
	 * @remark 
	 * 数据长度必须对等
	 */
	public static float[][][][] transform(float[][][][] x,int n,int c,int h,int w) {
		
		float[] temp = MatrixUtils.transform(x);
		
		return MatrixUtils.transform(temp, n, c, h, w);
	}
	
	/**
	 * 矩阵转置
	 * @return
	 */
	public static float[][] transpose(float[][] x){
		float[][] result = new float[x[0].length][x.length];
		
		Vector<Task<Object>> workers = new Vector<Task<Object>>();
		
		for(int i = 0;i<x.length;i++) {
			final int index = i;
			workers.add(new Task<Object>(index) {
				@Override
			    public Object call() throws Exception {
					for(int j = 0;j<x[0].length;j++) {
						result[j][index] = x[index][j];
					}
					return null;
				}
			});
		}
		
		TaskEngine.getInstance(threadNum).dispatchTask(workers);
		
		return result;
	}
	

	/**
	 * transform
	 * @param x
	 * @index ni * c * h * w + ci * h * w + hi * w + wi
	 * @return
	 */
	public static float[][][][] col2img(float[] x, int N, int C, int H, int W){
		
		float[][][][] result = new float[N][C][H][W];
		
		float[][] mat = to2DimenArray(x, N * H * W, C);
		
		Vector<Task<Object>> workers = new Vector<Task<Object>>();
		
		for(int n = 0;n<N;n++) {
			final int index = n;
			workers.add(new Task<Object>(index) {
				@Override
			    public Object call() throws Exception {
					for(int c = 0;c<C;c++) {
						
						for(int h = 0;h<H;h++) {
							
							for(int w = 0;w<W;w++) {
								
								result[index][c][h][w] = mat[index * H * W + h * W + w][c];
								
							}
							
						}

					}
					return null;
				}
			});
		}
		
		TaskEngine.getInstance(threadNum).dispatchTask(workers);
		
		return result;
	}
	
	/**
	 * transform
	 * @param x
	 * @index ni * c * h * w + ci * h * w + hi * w + wi
	 * @return
	 */
	public static float[][][][] col2imgV2(float[] x, int N, int C, int H, int W){
		
		float[][][][] result = new float[N][C][H][W];

		float[][] mat = to2DimenArray(x, N * H * W, C);
		
		OP2dTo4d job = new OP2dTo4d(mat, result, 0, mat.length - 1);
		
		ForkJobEngine.run(job);
		
		return result;
	}

	/**
	 * transform
	 * @param x
	 * @index ni * c * h * w + ci * h * w + hi * w + wi
	 * @return
	 */
	public static float[][][][] col2img(float[][] x, int N, int C, int H, int W){
		
		float[][][][] result = new float[N][C][H][W];
		
		Vector<Task<Object>> workers = new Vector<Task<Object>>();
		
		for(int n = 0;n<N;n++) {
			final int index = n;
			workers.add(new Task<Object>(index) {
				@Override
			    public Object call() throws Exception {
					for(int c = 0;c<C;c++) {
						
						for(int h = 0;h<H;h++) {
							
							for(int w = 0;w<W;w++) {
								
								result[index][c][h][w] = x[index * H * W + h * W + w][c];
								
							}
							
						}

					}
					return null;
				}
			});
		}
		
		TaskEngine.getInstance(threadNum).dispatchTask(workers);
		
		return result;
	}
	
	public static float[][] to2DimenArray(float[] x, int n, int d){
		float[][] result = new float[n][d];
		
		for(int i = 0;i<n;i++) {
			System.arraycopy(x, i * d, result[i], 0, d);
		}
		
		return result;
	}
	
	public static int[] shape(float[][][][] x) {
		return new int[] {x.length,x[0].length,x[0][0].length,x[0][0][0].length};
	}
	
	public static int[] shape(float[][] x) {
		return new int[] {x.length,x[0].length};
	}
	
}
