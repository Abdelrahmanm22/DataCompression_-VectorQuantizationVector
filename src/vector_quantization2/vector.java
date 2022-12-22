package vector_quantization2;

public class vector {
	double[][]data;
    int width,higth;
    vector(){
        data = null;
        width=0;
        higth=0;
    }
    vector(int w,int h){
        this.width = w;
        this.higth = h;
        this.data = new double [width][higth];
    }
    
    vector(double [][]d,int w,int h){
        this.data=d;
        this.width=w;
        this.higth=h;
    }

    public double[][] getData() {
        return data;
    }

    public int getWidth() {
        return width;
    }

    public int getHigth() {
        return higth;
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHigth(int higth) {
        this.higth = higth;
    }
    
    double getElementOfvector(int w,int h){
        return this.data[w][h];
    }
    void setElementOfvector(int data,int w,int h){
        this.data[w][h]=data;
    }
    double getNearst(vector v){
        double d=0;
        for(int i=0;i<width;i++){
            for(int j=0;j<higth;j++){
                double x=this.data[i][j]-v.getElementOfvector(i, j);
                d+=x*x;
            }
        }
        return d;
    }
}
