import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

public class DisneyOpt {
	private static int MAX_SIZE=36;
	private int numDiv;
	private int[][] crossDiv;
	private HashMap<String, int[]> indiv;
	private ArrayList<String> indivTable;
	private int numIndiv;
	private String ordfname;
	private HashMap<String, ArrayList<String>> order;
	private ArrayList<String> headerTable;
	private int numVisitor;
	private int maxTimeSlot;
	private ArrayList<int[]> stdSolution;
	private int stdSolutionLength;
	private HashMap<String, Integer> orderFreq;
	private HashMap<String, Integer> maxLoad;
	private ArrayList<String> hotDiv;

	public static void main(String[] args) throws Exception {
		String basepath = "/Desktop/";
		//basepath = "/home/jhyeh/Desktop/00MyDesktop/expr/disney/";
		basepath = "C:/";
		String fname1 = basepath+"crossdiv.txt";
		String fname2 = basepath+"indiv.txt";
		String fname3 = basepath+"order.txt";
		fname3 = basepath+"order_p3.txt.all";
		String fname4 = basepath+"maxLoad.txt";
		double ratio=0.1;
		DisneyOpt opt = new DisneyOpt(fname1, fname2, fname3, fname4, ratio);
		opt.go();
	}
	
	public DisneyOpt(String s1, String s2, String s3, String s4, double ratio) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(s1), "utf-8"));
		//�頛賂�貊��蕭嚙�	嚙踝蕭�����秘	�嚗瑕���蕭嚙�	嚙踝�蕭謍堆蕭嚙�	嚙踝蕭����▽嚙踝蕭	��▽嚙踝嚙踐�蕭嚙�	嚙踐�蕭謇萇��蕭嚙�
		String line=br.readLine();
		StringTokenizer st = new StringTokenizer(line, "\t");
		this.numDiv = st.countTokens();
		//System.err.println(numDiv);
		this.crossDiv = new int[this.numDiv][this.numDiv];
		int index=0;
		while ((line=br.readLine()) != null) {
			//System.out.println(line);
			//0	15	15	25	20	18	8
			st = new StringTokenizer(line, "\t");
			int index2=0;
			while (st.hasMoreTokens()) {
				//System.err.println(""+index+", "+index2);
				//this.crossDiv[index][index2++] = Double.parseDouble(st.nextToken().trim());
				this.crossDiv[index][index2++] = Integer.parseInt(st.nextToken().trim());
			}
			index++;
		}
		br.close();
		
		this.indiv = new HashMap<String, int[]>();
		this.indivTable = new ArrayList<String>();
		br = new BufferedReader(new InputStreamReader(new FileInputStream(s2), "Big5"));
		//��蕭嚙踝蕭嚙�	�蝞賃(�嚙�)	嚙踝蕭蹇蕭嚙�(嚙踝蕭��蕭嚙�)
		line=br.readLine();
		while ((line=br.readLine()) != null) {
			//System.out.println(line);
			//A1	30	30
			StringTokenizer st2 = new StringTokenizer(line, "\t");
			while (st2.hasMoreTokens()) {
				String name=st2.nextToken();
				int num1=Integer.parseInt(st2.nextToken().trim());
				int num2=Integer.parseInt(st2.nextToken().trim());
				this.indiv.put(name, new int[]{num1, num2});
				this.indivTable.add(name);
			}
		}
		br.close();
		this.numIndiv = this.indiv.keySet().size();
		
		this.orderFreq = new HashMap<String, Integer>(); 
		this.order = new HashMap<String, ArrayList<String>>();
		this.headerTable = new ArrayList<String>();
		br = new BufferedReader(new InputStreamReader(new FileInputStream(s3), "Big5"));
		int count=0;
		while ((line=br.readLine()) != null) {
			//System.out.println(line);
			//00020	B3	B2	C4	D2	E2	E9
			StringTokenizer st2 = new StringTokenizer(line, "\t");
			String userid=st2.nextToken();
			userid=""+count;
			count++;
			ArrayList<String> local = new ArrayList<String>();
			String s = st2.nextToken().trim();
			if (orderFreq.get(s) == null) orderFreq.put(s, 0);
			orderFreq.put(s, orderFreq.get(s)+1);
			while (st2.hasMoreTokens())	local.add(s);
			if (local.size() > 0) {
				this.headerTable.add(userid);
				this.order.put(userid, local);
				if (local.size() > this.maxTimeSlot) this.maxTimeSlot=local.size();
			}
		}
		br.close();
		this.stdSolution = this.standardSolution();
		this.numVisitor = order.keySet().size();
		//System.out.println("Total "+order.keySet().size()+" orders got.");
		
		this.maxLoad = new HashMap<String, Integer>();
		br = new BufferedReader(new InputStreamReader(new FileInputStream(s4), "Big5"));
		br.readLine();
		line="";
		while ((line=br.readLine()) != null) {
			st = new StringTokenizer(line, "\t");
			s1 = st.nextToken().trim();
			int i1 = (int)(Integer.parseInt(st.nextToken().trim())*ratio);
			maxLoad.put(s1, i1);
		}
		br.close();
		
		this.hotDiv = new ArrayList<String>();
		for (String key: orderFreq.keySet()) {
			int freq = orderFreq.get(key);
			int max = maxLoad.get(key);
			if (freq > max) this.hotDiv.add(key);
		}
	}

	private boolean isSolution(ArrayList<int[]> sol) {
    	for (int ts=0; ts<this.maxTimeSlot; ts++) {
    		//reviseTimeSlot(sol, ts);
    		HashMap<Integer, Integer> indivDist = new HashMap<Integer, Integer>();
    		for (int[] ii: sol) {
    			if (ii.length > ts) {
    				int indivID = ii[ts];
    				//System.err.println(indivID);
    				String indivName = this.indivTable.get(indivID);
    				int[] data = this.indiv.get(indivName);
    				int capacity = data[0];
    				if (indivDist.get(indivID) == null) indivDist.put(indivID, 0);
    				if (indivDist.get(indivID)+1 > capacity) {
    					System.err.println("Capacity overload: "+indivID);
    					return false;
    				}
    				indivDist.put(indivID, indivDist.get(indivID)+1);
    			}
    		}
    	}
		return true;
	}
	
	private int walkTime(int indiv1, int indiv2) {
		/*
		 	�頛賂�貊��蕭嚙�	嚙踝蕭�����秘	�嚗瑕���蕭嚙�	嚙踝�蕭謍堆蕭嚙�	嚙踝蕭����▽嚙踝蕭	��▽嚙踝嚙踐�蕭嚙�	嚙踐�蕭謇萇��蕭嚙�
			5	15	15	25	20	18	8
			15	5	15	25	30	25	20
			15	10	5	8	20	25	30
			25	25	8	5	8	20	35
			20	30	20	8	5	10	20
			18	25	25	20	10	5	10
			8	20	30	35	20	10	5
		 */
		String str1 = this.indivTable.get(indiv1);
		String str2 = this.indivTable.get(indiv2);
		// A1, B2, C3,...
		int i=str1.charAt(0)-'A';
		int j=str2.charAt(0)-'A';
		if (i == j) return 5;
		return this.crossDiv[i][j];
	}
	
	public double scheduleCost(ArrayList<int[]> sol) {
		double cost = 0;
		for (int[] ii: sol) {
			for (int idx=0; idx<ii.length-1; idx++) {
				cost += walkTime(ii[idx], ii[idx+1]);
			}
		}
		// process edit distance here!!!!!!!!!!!!!!!!!!!!!!!!!
		//int dist = EditDistance.editDistance(sol, this.stdSolution);
		//cost *= (1.0+dist/(double)this.stdSolutionLength); 
		return cost;
	}
	
	private ArrayList<int[]> copy(ArrayList<int[]> sol) {
		// perform deep copy
		ArrayList<int[]> result = new ArrayList<int[]>();
		for (int[] r: sol) result.add(r.clone());
		return result;
	}
	
	public ArrayList<int[]> mutate(ArrayList<int[]> r) {
    	ArrayList<int[]> sol = copy(r);
        int who = (int)(Math.random()*sol.size());
        int[] whosdata = sol.get(who);

        int i = (int)(Math.random()*whosdata.length);
        if (Math.random()<0.5 && i>0) {
        	int tmp=whosdata[i]; whosdata[i]=whosdata[i-1]; whosdata[i-1]=tmp;
        }
        else if (i<whosdata.length-1) {
        	int tmp=whosdata[i]; whosdata[i]=whosdata[i+1]; whosdata[i+1]=tmp;
        }
        else if (i>0) {
        	int tmp=whosdata[i]; whosdata[i]=whosdata[i-1]; whosdata[i-1]=tmp;
        }
        return sol;
    }

    public ArrayList<int[]> crossover(ArrayList<int[]> r1, ArrayList<int[]> r2) {
    	ArrayList<int[]> sol = copy(r1);
        int pos = (int)(Math.random()*sol.size());
        for (int i=pos; i<sol.size(); i++)
        	sol.set(i, r2.get(i));
        return sol;
    }
    
    private ArrayList<int[]> standardSolution() {
    	this.stdSolutionLength = 0;
    	ArrayList<int[]> sol = new ArrayList<int[]>();
    	for (String uid: this.headerTable) {
    		//G2	F3	S4	CS5	ES8	D1	G1
    		ArrayList<String> pdata = this.order.get(uid);
    		int[] local = new int[pdata.size()];
    		// convert to indiv id
    		for (int i=0; i<pdata.size(); i++) {
    			String ind = pdata.get(i);
    			int indid = indivTable.indexOf(ind);
    			if (indid < 0)
    				System.out.println("Wrong indiv ID: "+ind);
    			local[i] = indid;
    			this.stdSolutionLength++;
    		}
    		sol.add(local);
    	}
    	return sol;
    }
    
    private ArrayList<int[]> makeRandomSolution() {
    	ArrayList<int[]> sol = new ArrayList<int[]>();
    	for (String uid: this.headerTable) {
    		//G2	F3	S4	CS5	ES8	D1	G1
    		ArrayList<String> pdata = this.order.get(uid);
    		int[] local = new int[pdata.size()];
    		// convert to indiv id
    		for (int i=0; i<pdata.size(); i++) {
    			String ind = pdata.get(i);
    			int indid = indivTable.indexOf(ind);
    			if (indid < 0)
    				System.out.println("Wrong indiv ID: "+ind);
    			local[i] = indid;
    		}
    		// shuffle it
    		for (int i=0; i<local.length*3; i++) {
    			int pos1=(int)(Math.random()*local.length);
    			int pos2=(int)(Math.random()*local.length);
    			// swap
    			int tmp=local[pos2]; local[pos2]=local[pos1]; local[pos1]=tmp;
    		}
    		sol.add(local);
    	}
    	return sol;
    }
    
    /*public ArrayList<int[]> normalize(ArrayList<int[]> sol) {
    }*/
	
	public ArrayList<int[]> geneticOptimize() {
        int popsize = 100;
        double elite = 0.2;
        int maxiter = 100;
        double mutprob = 0.2;

        ArrayList<ArrayList<int[]>> pop = new ArrayList<ArrayList<int[]>>();
        //for (int i=0; i<popsize; i++) {
        while (pop.size() < popsize) {
        	ArrayList<int[]> sol = makeRandomSolution();
        	//ArrayList<int[]> sol2 = normalize(sol);
            //pop.add(sol2);
        	if (isSolution(sol)) pop.add(sol);
        	//System.out.println("First generation: population size="+(pop.size()*100/(double)popsize)+"%");
        	System.out.println("First generation: population size="+pop.size());
        }
        System.out.println("First generation complete.");

        int topelite=(int)(elite*popsize);

        int count=0;
        while (count++ < maxiter) {
        	System.err.println("Iteration "+count+" starts.");
            ASObject[] array = new ASObject[pop.size()];
            for (int i=0; i<pop.size(); i++) {
            	ArrayList<int[]> r = (ArrayList<int[]>)pop.get(i);
                double cost = this.scheduleCost(r);
                array[i] = new ASObject(i, cost);
            }
            Arrays.sort(array);
            ArrayList<ArrayList<int[]>> pop2 = new ArrayList<ArrayList<int[]>>();
            for (int i=0; i<topelite; i++) {
                pop2.add(pop.get(array[i].label));
            }
            pop = pop2;

            while (pop.size() < popsize) {
                if (Math.random() < mutprob) {
                	ArrayList<int[]> newsol=null;
                    boolean done=false;
                    while (!done) {
                    	 int c = (int)(Math.random()*topelite);
                     	 newsol = mutate(pop.get(c));
                    	 if (isSolution(newsol)) done=true;
                    }
                    pop.add(newsol);
                }
                else {
                    ArrayList<int[]> newsol=null;
                    boolean done=false;
                    while (!done) {
                        int c1 = (int)(Math.random()*topelite);
                        int c2 = (int)(Math.random()*topelite);
                    	newsol = crossover(pop.get(c1), pop.get(c2));
                    	if (isSolution(newsol)) done=true;
                    }
                    pop.add(newsol);
                }
            }
            //System.out.println("Current best cost = "+
            //        ((ASObject)array[0]).score+" in generation "+count);
        }

        return pop.get(0);
    }

    public void go() {
    	ArrayList<int[]> sol = this.geneticOptimize();
        //this.printSchedule(sol);
        double best = this.scheduleCost(sol);
        System.out.println("Cost = "+best);
        System.out.println("Average waiting time = "+((double)best)/sol.size());
    }
    
    public class ASObject implements Comparable<ASObject> {
        public int label;
        public double score;

        public ASObject(int l, double s) {
            this.label = l;
            this.score = s;
        }

        public int compareTo(ASObject other) {
            ASObject obj = (ASObject)other;
            if (this.score < obj.score) return -1;
            else if (this.score > obj.score) return 1;
            return 0;
        }
    }
}
