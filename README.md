# Hypothetical Answers
Hypothetical answers is an ongoing implementation of the paper **Hypothetical answers to continuous queries over data streams** by *Luís Cruz-Filipe*, *Graça Gasper* and *Isabel Nunes*.

The framework provided in the paper is a datalog based framework where every predicate has a temporal argument. Each instance of a predicate can then either be initiated to a specific time or to a time-variable.
Some predicates, called EDB predicates, will then be provided as information to the program over time with all true instances of EDB predicates with time 0 being provided at time 0. Anything not provided is presumed to be false.

Hypothetical answers allows the specification of such a program. The program can then be queried, and information can then be added dynamically as time advances.
The program will not only answer whether a query can be satisfied, but also what is required of the future information for the query to be satisfied, and what previous EDB instances support this answer.  

## Getting started
Create a Hypothetical.HypotheticalReasoner.

```Hypothetical.HypotheticalReasoner h = new HyptoheticalReasoner();```

Add some clauses.

```
h.addClause("Flag(X,T)<-Temp(X,high,T)");
h.addClause("Cool(X,T+1)<-Flag(X,T),Flag(X,T+1)");
h.addClause("Shdn(X,T+1)<-Cool(X,T),Flag(X,T+1)");
h.addClause("Malf(X,T-2)<-Shdn(X,T)");      
```
Then the system can be queried.
```
h.query("Malf(X,T)");
```
Now information can be added.
```
h.nextTime("Temp(wt25,high,0),Temp(wt12,high,0)");
h.nextTime("Temp(wt25,high,1),Temp(wt12,high,1)");
h.nextTime("Temp(wt25,high,2)");
h.nextTime("Temp(wt12,high,3)");
```
Printing the Hypothetical.HypotheticalReasoner will now show answers.
```
System.out.println(h);
```
Which should show:
```
Logic.Program:
Flag(X,T)<-Temp(X,high,T)
Cool(X,T+1)<-Flag(X,T),Flag(X,T+1)
Shdn(X,T+1)<-Cool(X,T),Flag(X,T+1)
Malf(X,T-2)<-Shdn(X,T)

Hypothetical Answers:
	[{},{Temp(X,high,T),Temp(X,high,T+1),Temp(X,high,T+2)}]

Evidence Answers:
	[{(X/wt12),(T/3)},{Temp(wt12,high,3)},{Temp(wt12,high,4),Temp(wt12,high,5)}]
	[{(X/wt12),(T/2)},{Temp(wt12,high,2),Temp(wt12,high,3)},{Temp(wt12,high,4)}]
	[{(X/wt12),(T/1)},{Temp(wt12,high,1),Temp(wt12,high,2),Temp(wt12,high,3)},{}]
	[{(X/wt25),(T/0)},{Temp(wt25,high,0),Temp(wt25,high,1),Temp(wt25,high,2)},{}]
	[{(X/wt12),(T/0)},{Temp(wt12,high,0),Temp(wt12,high,1),Temp(wt12,high,2)},{}]

Answers:
	{(X/wt12),(T/1)}
	{(X/wt25),(T/0)}
	{(X/wt12),(T/0)}

```
## Hypothetical.HypotheticalReasoner
//TODO
