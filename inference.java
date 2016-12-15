/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Problem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.ArrayList;

/**
 *
 * @author Shreya
 */
public class inference {

    /**
     * @param args the command line arguments
     */
    public static Scanner file;
    public static File output;
    public static PrintWriter outputwrite;
    public static PrintWriter traverselog;
    public static PrintWriter nextstate;  
    static TreeMap<Integer,String> query;
    static TreeMap<Integer,String> kb;
    static Set queryset;
    static Set kbset;
    static int[] variable=new int[26];
    
    public static void main(String[] args) {
        // TODO code application logic here
        try
        {
		File output=new File("output.txt");
        outputwrite=new PrintWriter(new FileWriter(output));  
        //file=new Scanner(new FileReader(args[1]));
        file=new Scanner(new FileReader("input_AI9.txt"));
        
        
        /***************QUERY*********************************/
        int n=Integer.parseInt(file.nextLine());
        query=new TreeMap<Integer,String>();
        for(int i=0;i<n;i++)
        {
            query.put(i,file.nextLine());
        }
        /* Display content using Iterator*/
        queryset = query.entrySet();
        Iterator qiterator = queryset.iterator();
        while(qiterator.hasNext()) 
        {
        Map.Entry qentry= (Map.Entry)qiterator.next();
        } 
        /***************END OF QUERY*********************************/ 
        
        /***************KB*********************************/
        int n1=Integer.parseInt(file.nextLine());
        kb=new TreeMap<Integer,String>();
        for(int i=0;i<n1;i++)
        {
            kb.put(i,file.nextLine());
        }
        
        /* Display content using Iterator*/
        kbset=kb.entrySet();
        Iterator kiterator= kbset.iterator();
        while(kiterator.hasNext()) 
        {
        Map.Entry kentry= (Map.Entry)kiterator.next();
        } 
        /***************END OF KB*********************************/ 
        
        file.close();
        
        //********************
        kbset=kb.entrySet();
        kiterator= kbset.iterator();
        while(kiterator.hasNext()) 
        {
        Map.Entry kentry= (Map.Entry)kiterator.next();
        }
        //*******************
        
        qiterator = queryset.iterator();
        while(qiterator.hasNext()) 
        {
            Map.Entry qentry=(Map.Entry)qiterator.next();
            String q=""+qentry.getValue();
            String ans=FOL_BC_ASK(q);
            outputwrite.println(ans);
        }
        outputwrite.close();
        
        }
        catch(Exception e)
        {
        }

    }
    public static ArrayList<String> FOL_BC_OR(String goal, String theta, ArrayList<String> kbvisited, ArrayList<String> goalsconstant)
    {
        ArrayList<String> answers=new ArrayList<String>();
      
        int openg=goal.indexOf("(");
        String grhs=(goal.substring(0,openg)).trim();
        
        //**********************************
        //LOOP DETECTION!!!
        if(isConstant(goal))
        {
            if(goalsconstant.contains(goal))
            {
                answers.add("FALSE");
                return answers;
            }
            
        }
        //******************************
        
        kbset = kb.entrySet();
        Iterator kiterator = kbset.iterator();
        
        
        while(kiterator.hasNext())  //FOR EVERY SENTENCE IN KB!!!
        {
            Map.Entry kentry=(Map.Entry)kiterator.next();
            String kbentry=""+kentry.getValue();
            String key=""+kentry.getKey();
            
            
            String lhsrhs[]={"",""};
            if(kbentry.contains(" => "))
            {
                lhsrhs=kbentry.split(" => ");
            }
            else
            {
                lhsrhs=new String[2];
                lhsrhs[0]="";
                lhsrhs[1]=kbentry;
            }   
            
            
                int open=lhsrhs[1].indexOf("(");
                String krhs=(lhsrhs[1].substring(0,open)).trim();
                
                if(grhs.equals(krhs)) //If the consequent matches!
                {
                    String stdkbentry=standardize(kbentry);
                    lhsrhs=new String[2];
                    if(kbentry.contains(" => "))
                    {
                        lhsrhs=stdkbentry.split(" => ");
                    }
                    else
                    {
                        lhsrhs=new String[2];
                        lhsrhs[0]="";
                        lhsrhs[1]=stdkbentry;
                    }
                    kbvisited.add(key);
                    String theta1=""+theta;
                    
                    String unification=UNIFY(lhsrhs[1].trim(),goal,theta);
                    String revunification=UNIFY(goal,lhsrhs[1].trim(),theta1);
                    
                    
                    if(!unification.equals("FALSE"))
                    {
                        
                        ArrayList<String> prevgoals=new ArrayList<String>(goalsconstant);
                        if(isConstant(goal))
                        {
                            prevgoals.add(goal);
                         
                        }
                        
                        ArrayList<String> ANDanswers=FOL_BC_AND(lhsrhs[0].trim(),unification,kbvisited,prevgoals);
                        
                        for(String answer: ANDanswers)
                        {
                            answers.add(answer);
                        }

                    }
      
                }
            }
        
        
        if(answers.isEmpty())
        {
            answers.add("FALSE");
        }
        
        return answers;
    }
    
    
    public static String standardize(String original)
    {
        //**********************STANDARDIZE 
        String sentori=""+original;
        String sentence=""+original;
        sentence=sentence.replace(" => ", "&");
        sentence=sentence.replace(" ^ ", "&");
        
        String predicates[]=sentence.split("&");
        ArrayList<String> replacement=new ArrayList<String>();
        ArrayList<String> var=new ArrayList<String>();
        for(String p: predicates)
        {
            int openbrace=p.indexOf("(");
            String a=p.substring(openbrace+1,p.trim().length()-1);
            String a1[]=a.split(",");
            
            for(String argument: a1)
            {
                if(!var.contains(argument)&&argument.matches("[a-z]"))
                {
                    var.add(argument);
                    int ascii=(int)argument.charAt(0)-97;
                    if(variable[ascii]>0)
                    {
                        replacement.add(argument+variable[ascii]);
                    }
                    else
                    {
                        replacement.add(argument);
                    }
                    variable[ascii]++;
                }
            }
        }
        
        for(int i=0;i<var.size();i++)
        {
            
            int index=0;
            String check="("+var.get(i)+",";
            if(sentori.contains(check))
            {
             String r="("+replacement.get(i)+",";   
             sentori=sentori.replace(check,r);
            }
            check=","+var.get(i)+",";
            if(sentori.contains(check))
            {
             String r=","+replacement.get(i)+",";      
             sentori=sentori.replace(check,r);
            }
            check=","+var.get(i)+")";
            if(sentori.contains(check))
            {
             String r=","+replacement.get(i)+")";         
             sentori=sentori.replace(check,r);
            }
            check="("+var.get(i)+")";
            if(sentori.contains(check))
            {
             String r="("+replacement.get(i)+")";         
             sentori=sentori.replace(check,r);
            }
        }
       
        return sentori;
        //********STANDARDIZE
        
    }
    
 public static ArrayList<String> FOL_BC_AND(String goals, String theta, ArrayList<String> kbvisited,ArrayList<String> prevgoals)
 {
        ArrayList<String> answers=new ArrayList<String>();
        System.out.println();
        System.out.println("FOL_BC_AND("+goals+",{"+theta+"},"+kbvisited+","+prevgoals+")");
        
        if(theta.equals("FALSE"))
        {
            answers.add("FALSE");
            return answers;
        }
        
        if(goals.equals(""))
        {
            answers.add(theta);
            return answers;
        }
        
        //separate all predicates in premise
        String goalsentence=""+goals;
        goalsentence=goalsentence.replace(" ^ ", "&");
        String predicates[]=goalsentence.split("&");
        
        String first=""+predicates[0];
        String rest=""+goals.replace(first,"");
        int index=rest.indexOf(" ^ ");
        if(index>-1)
        {
            rest=""+rest.substring(index+3);
        }
        String substitution=SUBST(theta,first); 
        ArrayList<String> ORans=FOL_BC_OR(substitution,theta,kbvisited,prevgoals);
        
        boolean atleastone=false;
        for(String thetadash: ORans)
        {
            if(!thetadash.equals("FALSE"))
            {
                atleastone=true;
                break;
            }
        }
        if(!atleastone)
        {
            answers.add("FALSE");
            return answers;
        
        }
        
        if(predicates.length==1)
        {
            for(String thetadash: ORans)
            {
                answers.add(thetadash);
            }
            
        }
        else
            for(String thetadash: ORans)
            {
                for(int i=1;i<predicates.length;i++)
                {
                    ArrayList<String> ANDans=FOL_BC_AND(rest,thetadash,kbvisited,prevgoals);
                    for(String thetadashdash: ANDans)
                    {
                        answers.add(thetadashdash);
                    }
                }
            }
        
        return answers;
    }
    
      public static String SUBST(String theta,String predicate)
    {
        String substh[]=theta.split(",");
        int indexopen=predicate.indexOf("(");
        String argsoriginal=predicate.substring(indexopen,predicate.length());
        String args=new String(argsoriginal);
        for(String sub:substh)
        {
            String replacement[]=sub.split("/");
            if(args.contains("("+replacement[0]+","))
            {
                args=args.replace("("+replacement[0]+",", "("+replacement[1]+",");
            }
            else if(args.contains(","+replacement[0]+","))
            {
                args=args.replace(","+replacement[0]+",", ","+replacement[1]+",");
            }
            else if(args.contains(","+replacement[0]+")"))
            {
                args=args.replace(","+replacement[0]+")", ","+replacement[1]+")");
            }
            else if(args.contains("("+replacement[0]+")"))
            {
                args=args.replace("("+replacement[0]+")", "("+replacement[1]+")");
            }
            
        }
        predicate=predicate.replace(argsoriginal, args);
        return predicate;
    }

    public static boolean isConstant(String goal)
    {
        int openg=goal.indexOf("(");
        
        //******************************
        //Constant Goal??
        String arguments=goal.substring(openg+1,goal.length()-1);
        String argsG[]=arguments.split(",");
       
        boolean isConstant=true;
        for(String arg1:argsG)
        {
            if(arg1.matches("[a-z][0-9]*"))
            {
                isConstant=false;
                break;
            }
        }   
        return isConstant;
    }
    
    public static String UNIFY(String x, String y, String theta)
    {
        
        if(theta.equals("FALSE"))
        {
                return "FALSE";
        }
        if(x.equals(y))
        {
            return theta;     
        }    
        else if(x.matches("[a-z][0-9]*")) //x is a variable
        {
            String xvar=UNIFY_VAR(x,y,theta);
            return xvar;
        }
        else if(y.matches("[a-z][0-9]*")) //y is a variable
        {
            String yvar=UNIFY_VAR(y,x,theta);
            return yvar;
        }
        else if(x.contains("(")&&y.contains("("))
        {
            int openx=x.indexOf("(");
            String xOP=(x.substring(0,openx)).trim();
            int openy=y.indexOf("(");
            String yOP=(y.substring(0,openy)).trim();    
            
            String xARGS=""+x;
            xARGS=xARGS.replace(xOP+"(", "");
            xARGS=xARGS.replace(")", "");
            
            String yARGS=""+y;
            yARGS=yARGS.replace(yOP+"(", "");
            yARGS=yARGS.replace(")", "");
            
            String comp=UNIFY(xARGS,yARGS,UNIFY(xOP,yOP,theta));
            return comp;
        }
        else if(x.contains(",")&&y.contains(","))// TO DO!!!!
        {
            String xele[]=x.split(",");
            String yele[]=y.split(",");
            int cele=xele.length;
            for(int i=0;i<cele;i++)
            {
                theta=UNIFY(xele[i],yele[i],theta);
            }
            return theta;
        }
        return "FALSE";
    }
    
    
    public static String UNIFY_VAR(String var, String x, String theta)
    {
            String check=""+var+"/";
            String check2=""+x+"/";
            if(theta.contains(check))
            {
                int varindex1=theta.indexOf(""+var+"/")+var.length()+1;
                int varindex2=theta.indexOf(",",varindex1);
                if(varindex2<0)
                varindex2=theta.length();
                String val=theta.substring(varindex1, varindex2);
                String a1=UNIFY(val,x,theta);
                return a1;
            }
            else if(theta.contains(check2))
            {
                int varindex1=theta.indexOf(""+x+"/")+x.length()+1;
                int varindex2=theta.indexOf(",",varindex1);
                if(varindex2<0)
                varindex2=theta.length();
                String val=theta.substring(varindex1, varindex2);
                String a1=UNIFY(var,val,theta);
                return a1;
            }
        
              
              if(theta.equals(""))
                theta=var+"/"+x;
              else
                theta+=","+var+"/"+x;
       
              return theta;
            
       
    }

    
    public static String FOL_BC_ASK(String query)
    {
        ArrayList<String> kbvisited=new ArrayList<String>(); //Removing Loops!!!
        ArrayList<String> goalsconstant=new ArrayList<String>(); //Removing Loops!!!
        ArrayList<String> substitutions=FOL_BC_OR(query,"",kbvisited,goalsconstant);
        System.out.println("\n Final:"+substitutions);
        
        for(String s:substitutions)
        {
            if(!s.equals("FALSE"))
            {
                return "TRUE";
            }
        }
        return "FALSE";
    }
    
    
    
    
     
}
