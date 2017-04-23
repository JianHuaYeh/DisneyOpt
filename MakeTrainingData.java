import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;


public class MakeTrainingData {
	private String orderFile1;
	private String orderFile2;
	private String orderFile3;
	private int recCount1;
	private int recCount2;
	private int recCount3;
	private HashMap<String, Integer> maxLoad;
	
	public static void main(String[] args) {
		String path = "/Desktop/";
		path = "C:/";
		String ofname1 = path+"order_p1.txt";
		String ofname2 = path+"order_p2.txt";
		String ofname3 = path+"order_p3.txt";
		double pct1 = 0.285;
		double pct2 = 0.524;
		double pct3 = 0.191;
		String fname = path+"maxLoad.txt";
		int max = 50000;
		double ratio=0.1;
		MakeTrainingData mtd = new MakeTrainingData(ofname1, ofname2, ofname3, pct1, pct2, pct3, fname, max, ratio);
		mtd.go();
	}
	
	public MakeTrainingData(String ofname1, String ofname2, String ofname3, double pct1,
			double pct2, double pct3, String fname, int max, double ratio) {
		this.orderFile1 = ofname1;
		this.orderFile2 = ofname2;
		this.orderFile3 = ofname3;
		this.recCount1 = (int)(max*pct1);
		this.recCount2 = (int)(max*pct2);
		this.recCount3 = max-recCount1-recCount2;
		System.out.println("Data count distribution: "+recCount1+","+recCount2+","+recCount3+", max="+max);
		this.maxLoad = new HashMap<String, Integer>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fname), "Big5"));
			br.readLine();
			String line="";
			while ((line=br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, "\t");
				String s1 = st.nextToken().trim();
				int i1 = (int)(Integer.parseInt(st.nextToken().trim())*ratio);
				maxLoad.put(s1, i1);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	public void go() {
		try {
			ArrayList<String> recAll = new ArrayList<String>();
			ArrayList<String> recs = new ArrayList<String>();
			BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(this.orderFile1), "utf-8"));
			String line="";
			while ((line=br1.readLine()) != null) {
				recs.add(line.trim());
			}
			br1.close();
			int count=0;
			int idx=0;
			while (count < recCount1) {
				recAll.add(recs.get(idx));
				idx = (idx+1)%recs.size();
				count++;
			}
			System.out.println("File1: "+this.orderFile1+" produce "+count+" records.");

			recs = new ArrayList<String>();
			BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(this.orderFile2), "utf-8"));
			line="";
			while ((line=br2.readLine()) != null) {
				recs.add(line.trim());
			}
			br2.close();
			count=0;
			idx=0;
			while (count < recCount2) {
				recAll.add(recs.get(idx));
				idx = (idx+1)%recs.size();
				count++;
			}
			System.out.println("File2: "+this.orderFile2+" produce "+count+" records.");

			recs = new ArrayList<String>();
			BufferedReader br3 = new BufferedReader(new InputStreamReader(new FileInputStream(this.orderFile3), "utf-8"));
			line="";
			while ((line=br3.readLine()) != null) {
				recs.add(line.trim());
			}
			br3.close();
			count=0;
			idx=0;
			while (count < recCount3) {
				recAll.add(recs.get(idx));
				idx = (idx+1)%recs.size();
				count++;
			}
			System.out.println("File3: "+this.orderFile3+" produce "+count+" records.");
			
			// now shuffle it
			int size=recAll.size();
			for (int i=0; i<size; i++) {
				int idx1=(int)(size*Math.random());
				int idx2=(int)(size*Math.random());
				String s1 = recAll.get(idx1);
				recAll.set(idx1, recAll.get(idx2));
				recAll.set(idx2, s1);
			}
			
			BufferedWriter bw3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.orderFile3+".all"), "utf-8"));
			HashMap<String, Integer> loadStatus = new HashMap<String, Integer>();
			for (String s: recAll) {
				StringTokenizer st = new StringTokenizer(s, "\t");
				String outstr=st.nextToken().trim();
				while (st.hasMoreTokens()) {
					String id = st.nextToken().trim();
					//System.out.println("id=["+id+"], s=["+s+"]");
					if (loadStatus.get(id) == null) loadStatus.put(id, 0);
					int curr = loadStatus.get(id)+1;
					int max = this.maxLoad.get(id);
					if (curr <= max) {
						loadStatus.put(id, curr);
						outstr += "\t"+id;
					}
				}
				bw3.write(outstr);
				bw3.newLine();
			}
			bw3.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
