package vector_quantization2;

import java.awt.image.BufferedImage;
//import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.lang.Math;
import java.io.FileWriter;

public class Quantization {
	ArrayList<vector> Vectors;
    ArrayList<vector> CodeBook;
    int width,height; // width and length for picture
    int x,y,no_of_vectors;
    int[][] pixel ;
    ArrayList<Integer> encoder;
    BufferedImage image;
    File path;
    Quantization(){
        Vectors = new ArrayList<>();
        CodeBook = new ArrayList<>();
        encoder = new ArrayList<>();
        pixel =null;
        image =null;
        path = null;
    }
    void creatAllVectors(){
        for(int i=0;i<width;i+=x){
            for(int j=0;j<height;j+=y){
                vector newVector = new vector(x,y);
                for(int a=0;a<x;a++){
                    for(int b=0;b<y ;b++){
//                        if(i+a<width || j+b<height){
//                            newVector.setElementOfvector(pixel[i+a][j+b], b, b);
//                        }
                    	if(i+a>=width || j+b>=height)
                    		continue;
                    	newVector.setElementOfvector(pixel[i+a][j+b], b, b);
                    }
                }
                Vectors.add(newVector);
            }
        }
    }
    vector calcAvg(ArrayList<vector> allVec){
        double data [][] =new double[x][y]; 
        for(int i=0;i<x;i++){
            for(int j=0;j<y;j++){
                data[i][j]=0;
                for(vector v:allVec){
                    data[i][j]+=v.getElementOfvector(i, j);
                }
                data[i][j]/=allVec.size();
            }
        }
        vector newAvgVector = new vector(data,x,y);
        return newAvgVector;
    }
    void split(){
        ArrayList<vector> newCodebook = new ArrayList<>();
        for(vector v:CodeBook){
            vector left = new vector( x , y);
            vector right = new vector( x , y);
            for(int i=0;i<x;i++){
                for(int j=0;j<y;j++){
//                    int d= (int)v.data[i][j];
                    left.data[i][j]=(int) Math.ceil(v.data[i][j]-1);
                    right.data[i][j]=(int) Math.floor(v.data[i][j]+1);
                }
            }
            newCodebook.add(left);
            newCodebook.add(right);
            
        }
        CodeBook = newCodebook;
    }
    
	ArrayList<ArrayList<vector>> getNearestVector(){
        ArrayList<ArrayList<vector>> allNearst = new ArrayList<>();
        for(int i=0;i<CodeBook.size();i++){
            allNearst.add(new ArrayList<vector>());
        }
        for(int i=0;i<Vectors.size();i++){
            double min=Integer.MAX_VALUE;
            int index=0;
            for(int j=0;j<CodeBook.size();j++){
                double distance =  CodeBook.get(j).getNearst(Vectors.get(i));
                if(distance<min){
                    min=distance;
                    index = j;
                }
            }
            allNearst.get(index).add(Vectors.get(i));
        }
        return allNearst;
    }
    
    void creatCodeBooks(){
        CodeBook.add(calcAvg(Vectors));
        while(no_of_vectors>CodeBook.size()){
            split();
            ArrayList<ArrayList<vector>> nearstVectors = getNearestVector();
            for(int i=0;i<CodeBook.size();i++){
                if(nearstVectors.get(i).size()>0)
                    CodeBook.set(i, calcAvg(nearstVectors.get(i)));
            }
        }
        ArrayList<vector> previous=CodeBook;
        while(true){
            ArrayList<ArrayList<vector>> nearstVectors = getNearestVector();
            for(int i=0;i<CodeBook.size();i++){
                if(nearstVectors.get(i).size()>0)
                CodeBook.set(i, calcAvg(nearstVectors.get(i)));
            }
            if(CodeBook.equals(previous)){
                break;
            }
            previous = CodeBook;
        }
    }
    
    void encode() throws IOException{
    	FileWriter myWriter = new FileWriter("encode.txt");
        for(int i=0;i<Vectors.size();i++){
            double min=Integer.MAX_VALUE;
            int index=0;
            for(int j=0;j<CodeBook.size();j++){
                double distance =  CodeBook.get(j).getNearst(Vectors.get(i));
                if(distance<min){
                    min=distance;
                    index = j;
                }
            }
            myWriter.write(index);
            encoder.add(index);
        }
        myWriter.close();
        
    }
    BufferedImage decode() {
    	int  Reconstructed_Image [][] = new int [width][height];
    	int i=0,j=0,cnt=0;
    	for(int k=0;k<encoder.size();k++) {
    		for(int a=0;a<x;a++){
                for(int b=0;b<y ;b++){
                    if(i+a>=width || j+b>=height){
                    	continue;
                    }
                    Reconstructed_Image[i+a][j+b]=(int)CodeBook.get(encoder.get(k)).getElementOfvector(a, b);
                }
            }
    		cnt++;
    		if(cnt%((height+y-1)/y)>0) {
    			j+=y; //j=2
    		}else {
    			i+=x; //i=2
    			j=0;
    		}
    		
    	}
    	
    	BufferedImage image2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image2.setRGB(x, y,(Reconstructed_Image[x][y]<<16) |(Reconstructed_Image[x][y]) | (Reconstructed_Image[x][y]<<8)  );
            }
        }
    	return image2;

    }
    
    void getDataImage() {
    	width = image.getWidth();
        height = image.getHeight();
//        System.out.println(width+" "+height);
        pixel = new int [width][height];
        int rgb;
        for(int i=0;i<width;i++) {
        	for(int j=0;j<height;j++) {
        		try {
        			rgb =image.getRGB(i, j);
        			pixel[i][j] = (rgb>>16) & 0xff;
        		}catch(ArrayIndexOutOfBoundsException e) {
//        			System.out.println(i+" "+j);
        			System.out.println(e.getMessage());
        			
        			return;
        		}
        	}
        }
    }
    void readImage() {
    	try {
//    		System.out.println(path);
    		image = ImageIO.read(path);
    	}catch(Exception e){
    		System.out.println(e.getMessage());
    	}
    	
    }
    void writeImag(BufferedImage image) throws IOException {
    	try {
    		File file = new File(path.getParent()+"\\afterDecoded.png");
        	ImageIO.write(image, "jpg", file);
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    	
    }  
    void getDecode() throws IOException {
    	writeImag(decode());
    }
    
    void readFromUser() throws IOException {
    	Scanner input =new Scanner(System.in);
    	System.out.print("Enter length of vector: ");
    	this.x=input.nextInt();
    	
    	System.out.print("Enter width of vector: ");
    	this.y=input.nextInt();
    	System.out.print("Enter number of vectors: ");
    	this.no_of_vectors =input.nextInt();
    	path = new File("D:\\University\\Data Compression\\Labs\\Vector_Quantization\\vector_quantization2\\before.jpeg");
    	readImage();
    	getDataImage();
    	creatAllVectors();
    	creatCodeBooks();
    	encode();
    	getDecode();
    }
}
