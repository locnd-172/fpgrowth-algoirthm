# Frequent pattern mining of invoices dataset

## Brief introduction
The project uses retail invoice data of an online store for different countries
**France**, **United Kingdom**, **Portugal**, **Sweden** from December 1, 2010 - December 9, 2011. (Access
at: http://archive.ics.uci.edu/ml/datasets/Online+Retail).


The algorithm used is self-deployed FP-Growth, performing the search for requent item sets.
More advanced related issues such as *Closed Itemsets, Association Rules, Correlations, etc.* will not be within the scope of this project.

## Problem statement
Given the set of items $𝐼 = {𝑖_1, 𝑖_2, … , 𝑖_𝑚}$ and the database $𝐷$ are transactions,
each transaction is a subset of the set of items $𝐼 \space (𝑇 \subseteq 𝐼)$.

Find all subsets of I that frequently occur together
in transactions.

## Project goals
In this report, we will talk about the concept of frequent sets, FP-Growth algorithm, and present how to apply the FP-Growth algorithm to do mine frequent sets of orders from the sales invoices.

The topic of frequent set mining is really vast. In the scope of the subject, We focus on discussing a specific method of application frequent set mining using the FP-Growth algorithm. 

We will answer the questions: 

1. What is a FREQUENT ITEM SET? What does it mean, what role does it play?
2. How does the FP-Growth algorithm work? Using the FP-Growth algorithm to solve the problem of finding the frequent set of sales invoices.
3. Effectiveness of FP-Growth algorithm and improvement measures if any?


With what was mentioned above, the topic will focus on the following issues:
- Analyze, implement FP-Growth algorithm in
Java language.
- Analyze data set of online sales invoices of UK, France, Portual, Sweden, find popular item sets using the developed FP-Growth algorithm.
- Compare the results of the self-implemented program with the results from the Weka tool.
- Evaluate the results.

---
For more detail please find in *[Project report] FPGrowth algorithm.pdf* file.

Contributors: [locnd-172](https://github.com/locnd-172), [KhangNV0701](https://github.com/KhangNV0701)
