package com.horsehour.gene;
import java.text.DecimalFormat;
import java.util.BitSet;

public class NeoGA{
	private BitSet genes;
	private int geneLength;
	private double rangeInf;
	private double rangeSup;
	private int resolution;
	public NeoGA(){}
	public NeoGA(int geneLength,double rangeInf,double rangeSup,int resolution){
		this.genes=new BitSet(geneLength);
		this.geneLength=geneLength;
		this.rangeInf=rangeInf;
		this.rangeSup=rangeSup;
		this.resolution=resolution;
		double rand=randomer();			
		encoder(rand);
	}
	
	private double randomer(){
		double ret=rangeInf+Math.random()*(rangeSup-rangeInf);
		StringBuilder zeroStr=new StringBuilder();
		for(int i=0;i<resolution;i++){
			zeroStr.append("0");
		}
		DecimalFormat dFormat=new DecimalFormat("#."+zeroStr.toString());
		return new Double(dFormat.format(ret));
	}
	
//	由十进制数生成二进制串
	private BitSet encoder(double rand){
//		计算与rangeInf相隔倍数
		int multi=(int)((rand-rangeInf)*Math.pow(10, resolution));
		String geneString=Integer.toBinaryString(multi);
		int focus=geneLength-1;
		if(genes!=null){
			genes.clear();
		}
		for(int i=geneString.length()-1;i>=0;i--){
			if('1'==geneString.charAt(i)){
				genes.set(focus);
			}
			focus--;
		}
		return genes;
	}
	
//	将二进制转化为十进制
	private double decoder(){
		double ret=0D;
		for(int i=0;i<geneLength;i++){
			if(genes.get(i))
				ret+=Math.pow(2, geneLength-1-i);
		}
		return ret/Math.pow(10, resolution)+rangeInf;
	}
	
//	与其他个体交换指定范围的基因序列
	private void exchangeRange(NeoGA ind,int fromIndex,int toIndex){
		if(toIndex>=fromIndex){
			for(int i=fromIndex;i<=toIndex;i++){
				if(this.genes.get(i)^ind.genes.get(i)){
					if(this.genes.get(i)==false){
						this.genes.set(i);
						ind.genes.clear(i);
					}else{
						this.genes.clear(i);
						ind.genes.set(i);
					}
				}
			}
		}
	}
	
	public int getGeneLength(){
		return geneLength;
	}
//	获取编码实例	
	public BitSet getGenes(){
		return genes;
	}
//	确定指定位置上的二进制符号0/1
	public int getGeneAt(int position){
		if(genes.get(position)){
			return 1;
		}
		return 0;
	}
	private void printGene(){
		System.out.print("value="+decoder()+":");
		for(int i=0;i<geneLength;i++){
			System.out.print("\t"+getGeneAt(i));
		}
		System.out.println("");
	}
	public static void main(String[] args){
		NeoGA ga1=new NeoGA(8,0,255,0);
		NeoGA ga2=new NeoGA(8,0,255,0);
		ga1.printGene();
		ga2.printGene();
		System.out.println("====before====");
		ga1.exchangeRange(ga2, 2, 4);
		ga2.encoder(ga1.decoder());
		ga1.genes.set(1);
		ga1.printGene();
		ga2.printGene();
		System.out.println("====after====");
	}
}	
