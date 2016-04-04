//Import packages
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class PageRank
{
    static RationalNumber weights[][] = null;
    static int dim = 1;
    static boolean isRational = false;
    
    //Get Input
    static void getInput()
    {
        String input, s;
        String tokens[];
        boolean getDimension = false;
        int m = 0, n = 0;
        Scanner sc = new Scanner(System.in);
        do           
        {
            //Remove unnecessary spaces and tabs
            input = sc.nextLine();
            input = input.trim();
            input = input.replaceAll("\\t", " ");
            input = input.replaceAll("\\s+", " ");
            if(input.equals(""))
                continue;
            tokens = input.split(" ");  
            //check input for comments
            for (String token : tokens)
            {
                if (token.charAt(0) == '#')
                {
                    break;
                }
                //check given matrix-size
                else if(getDimension == false)
                {
                    dim = Integer.parseInt(token.substring(0,1));
                    token = token.toLowerCase();
                    if((token.charAt(0)) != token.charAt(2))
                    {
                        System.out.println("Invalid matrix size");
                        System.exit(0);
                    }
                    weights = new RationalNumber[dim][dim];
                    getDimension = true;
                }
                //validate values for matrix input
                else
                {
                    if(tokens.length != dim)
                    {
                        System.out.println("Invalid Input");
                        System.exit(0);
                    }
                    if(token.contains("/"))
                    {
                        weights[m][n] = new RationalNumber(Double.parseDouble(token.substring(0, token.indexOf('/'))), Double.parseDouble(token.substring(token.indexOf('/') + 1, token.length())));
                        isRational = true;
                        n++;
                    }
                    else
                    {
                        weights[m][n] = new RationalNumber(Double.parseDouble(token));
                        n++;
                    }
                    if(n == dim)
                    {
                        n = 0;
                        m++;
                    }
                }
            }
        }while(m < dim);
    }
    
    //validate matrix; check for negative input and column probability
    static void validateMatrix()
    {
        for(int i = 0; i < dim; i++)
        {
            for(int j = 0; j < dim; j++)
            {
                if(weights[i][j].num < 0 || weights[i][j].den < 0)
                {
                    System.out.println("Negative Input! Exit.");
                    System.exit(0);
                }
            }
        }
        
        double count = 0;
        for(int j = 0; j < dim; j++)
        {
            for(int i = 0; i < dim; i++)
            {
                count = count + (weights[i][j].num/weights[i][j].den);
            }
            double x = count;
            if(x == 0)
            {
                continue;
            }
            else if(x < 0.99 || x > 1.000001)
            {
                //System.out.println(count);
                System.out.println("Invalid Data! Probability not 1");
                System.exit(0);
            }
            else
            {
                //System.out.println(count);
                count = 0;
            }
        }
    }
    
    //remove sink nodes that point only to themselves
    static void removeSink()
    {
        int m = 0, n;
        for(int i = 0; i < dim; i++)
        {
            double x = weights[i][i].num/weights[i][i].den;
            
            if(x == 1.0)
            {
                RationalNumber rn[][] = new RationalNumber[dim -1][dim-1];
                
                for(int j = 0; j < dim; j++)
                {
                    if(j == i)
                    {
                        continue;
                    }
                    n = 0;
                    for(int k = 0; k < dim; k++)
                    {
                        if(k == i)
                        {
                            continue; 
                        }
                        else
                        {
                            rn[m][n] = new RationalNumber(weights[j][k].num, weights[j][k].den); 
                            n++;
                        }
                    }
                    m++;
                }
                dim--;
                
                weights = new RationalNumber[dim][dim];
                for(int j = 0; j < dim; j++)
                {
                    for(int k = 0; k < dim; k++)
                    {
                        weights[j][k] = new RationalNumber(rn[j][k].num, rn[j][k].den);
                    }
                }
                removeSink();
                break;
            }
        }
    }
    
    //Remove nodes that do not point to any page
    static void removeSink1()
    {
        int i = -1, count = 0;
        for(int j = 0; j < dim; j++)
        {
            for(int k = 0; k < dim; k++)
            {
                if(weights[k][j].num == 0.0)
                    count++;
            }
            if(count == dim)
            {
                i = j;
                break;
            }
            else
                count = 0;
        }
        int m = 0, n;
        if(i != -1)
        {
            RationalNumber rn[][] = new RationalNumber[dim -1][dim-1];
                
            for(int j = 0; j < dim; j++)
            {
                if(j == i)
                {
                    continue;
                }
                n = 0;
                for(int k = 0; k < dim; k++)
                {
                    if(k == i)
                    {
                        continue; 
                    }
                    else
                    {
                        rn[m][n] = new RationalNumber(weights[j][k].num, weights[j][k].den); 
                        n++;
                    }
                }
                m++;
            }
            dim--;
                
            weights = new RationalNumber[dim][dim];
            for(int j = 0; j < dim; j++)
            {
                for(int k = 0; k < dim; k++)
                {
                    weights[j][k] = new RationalNumber(rn[j][k].num, rn[j][k].den);
                }
            }
            removeSink1();
        }
    }
    
    //Balance matrix weights/page weights
    static void balance()
    {
        for(int i = 0; i < dim; i++)
        {
            double sum = 0.0;
            int count = 0;
            for(int j = 0; j < dim; j++)
            {
                if(weights[j][i].num > 0.0)
                {
                    sum += weights[j][i].num;
                    count++;
                }
            }
            if(sum < 0.99 || sum > 1.01)
            {
                for(int k = 0; k < dim; k++)
                {
                    if(weights[k][i].num != 0.0)
                    {
                        weights[k][i].num = 1;
                        weights[k][i].den = count;
                    }
                }
            }
            
        }
    }
    
    //Calculate the rank
    static void calculateRank()
    {
        RationalNumber A[][];
        RationalNumber beta;
        RationalNumber beta1;
        RationalNumber N[][];
        RationalNumber r[] = new RationalNumber[dim];
        RationalNumber r1[] = new RationalNumber[dim];
       
        A = new RationalNumber[dim][dim];
        beta = new RationalNumber(7.0, 8.0);
        beta1 = new RationalNumber(1.0, 8.0);

        N = new RationalNumber[dim][dim];

        BigInteger a, b, c;

        //create N-matrix
        for(int i = 0; i < dim; i++)
        {
            for(int j =0; j < dim; j++)
            {
                N[i][j] = new RationalNumber(1.0, (double)dim);
            }
        }
        //create r-vector
        for(int i = 0; i < dim; i++)
        {
            r[i] = new RationalNumber(1.0, (double)dim);
            r1[i] = new RationalNumber(1.0, (double)dim);
        }
        
        //create A-matrix
        for(int i = 0; i < dim; i++)
        {
            for(int j =0; j < dim; j++)
            {
                double num = beta.num * weights[i][j].num;
                double den = beta.den * weights[i][j].den;
                A[i][j] = new RationalNumber(num, den);

                N[i][j].num = beta1.num * N[i][j].num;
                N[i][j].den = beta1.den * N[i][j].den;

                A[i][j].num = A[i][j].num * N[i][j].den + N[i][j].num * A[i][j].den;
                A[i][j].den = A[i][j].den * N[i][j].den;
            }
        }
        for(int i = 0; i < dim; i++)
        {
            for(int j =0; j < dim; j++)
            {
                System.out.print(A[i][j] + "\t");
            }
            System.out.println();
        }
        int itNo = 0;
        double diff = 1;
        double reqDiff = 0.000000000001;
        
        //iteratively calculate page rank
        do
        {
            for(int i = 0; i <dim; i++)
            {
                r[i].num = r1[i].num;
                r[i].den = r1[i].den;
            }
            
            for(int i = 0; i < dim; i++)
            {
                for(int j = 0; j < dim; j++)
                {
                    double dn = A[i][j].num * r[j].num;
                    double dd = A[i][j].den * r[j].den;
                    if(j == 0)
                    {
                        r1[i].num = r[j].num * A[i][j].num;
                        r1[i].den = r[j].den * A[i][j].den;
                    }
                    else
                    {
                        r1[i].num = r1[i].den * dn + r1[i].num * dd;
                        r1[i].den = r1[i].den * A[i][j].den * r[j].den;
                    }
                }
            }
            if(isRational)
            {
                BigInteger bi1, bi2, bi3;
                for(int i = 0; i < dim; i++)
                {
                    bi1 = new BigDecimal(r1[i].num).toBigInteger();
                    bi2 = new BigDecimal(r1[i].den).toBigInteger();
                    bi3 = bi1.gcd(bi2);
                    int bd = bi3.intValue();
                    r1[i].num = r1[i].num/bd;
                    r1[i].den = r1[i].den/bd;
                }
            }
           
            itNo++;
            
            //display each iteration
            System.out.println("Iteration " + itNo);
            display(r1, itNo);
            
            double n1,n2,d1,d2;
            if(isRational)
            {
                n1 = r[0].num;
                d1 = r[0].den; 
                n2 = r1[0].num;
                d2 = r1[0].den;
                d1 = n1 / d1;
                d2 = n2 / d2;
            }
            else
            {
                d1 = r[0].num;
                d2 = r1[0].num;
            }
            diff = Math.abs(d1 - d2);
        }while(diff > reqDiff);
    }
    
    static void display()
    {
        if(isRational)
        {
            for(int i = 0; i < dim; i++)
            {
                for(int j = 0; j < dim; j++)
                {
                    System.out.print(weights[i][j].num + "/" + weights[i][j] + "\t");
                }
                System.out.println();
            }
            System.out.println();
        }
        else
        {
            for(int i = 0; i < dim; i++)
            {
                for(int j = 0; j < dim; j++)
                {
                    double n = weights[i][j].num;
                    double d = weights[i][j].den;
                    System.out.print(new DecimalFormat("#0.000000000000").format(n/d) + "\t");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
    
    static void display(RationalNumber[] r, int c)
    {
        if(isRational && c < 6)
        {
            for(int i = 0; i < dim; i++)
            {
                System.out.println(r[i].num + "/" + r[i].den + "\t");
            }
            System.out.println();
        }
        else
        {
            for(int i = 0; i < dim; i++)
            {
                double n = r[i].num;
                double d = r[i].den;
                System.out.println(new DecimalFormat("#0.000000000000").format(n/d));
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args)
    {
        getInput();
        validateMatrix();
        removeSink();
        removeSink1();
        System.out.println("Matrix after removing sink nodes: ");
        display();
        balance();
        System.out.println("Matrix after balancing weights: ");
        display();
        calculateRank();
    }
}

//Create class to store rational numbers
class RationalNumber
{
    double num;
    double den;
    RationalNumber(Double n, Double d)
    {
        num = n;
        den = d;
    }
    RationalNumber(double n)
    {
        num = n;
        den = 1.0;
    }
   
    public String toString()
    {
        return num+"/"+den;
    }
}
