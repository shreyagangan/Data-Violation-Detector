# Data-Violation-Detector
 Using Backward Chaining Algorithm for First-Order Logic to give users an insight on the kind of private data that their shared data can infer  (Java)

# Problem:  
Given a knowledge base and a number of queries determine if the queries can be inferred from the knowledge base or not. Used backward chaining algorithm for solving this problem.  

# Input Format: 

The first line of the input will be the number of queries (n). 
Following n lines will be the queries, one per line. For each of them, we determine whether it can be proved form the knowledge base or not. 
Next line of the input will contain the number of clauses in the knowledge base (m).  
Following, there will be m lines each containing a statement in the knowledge base. 
Each clause is in one of these two formats:  
1- p1 ∧ p2 ∧ ... ∧ pn => q  
2- facts: which are atomic sentences. Such as p or ~p   
  
All the ps and also qs are either a literal such as HasPermission(Google,Contacts) or negative of a literal such as ~HasPermission(Google,Contacts).   
 Queries will not contain any variables.    
 Variables are all single lowercase letters   
 All predicates (such as HasPermission) and constants (such as Google) are case-sensitive alphabetical strings that begin with uppercase letters.    
 Each predicate has at least one argument. (There is no upper bound for the number of arguments). Same predicate will not appear with different number of arguments.    
 See the sample inputs for spacing patterns.    
 All of the arguments of the facts are constants. i.e. Assumption: there will be no fact such as HasPermission(x,Contacts) ( which says that everyone has permission to see the contacts!) in the knowledge base.    
