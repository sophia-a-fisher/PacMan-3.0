public class Java3208
{
   public static void main(String args[])
   {
      System.out.println("CALLING RECURSIVE METHOD");
      System.out.println("m5(6) == " + m9(4,3));
   }
   
   public static int m3(int n)
   {
      if(n == 1)
      {
         return 25;
      }   
      
      else
      {
         return n + m3(n-1);
      }
      
   }
   
   public static int m4(int n)
   {
      if(n == 5)
      {
         return 0;
      }   
      
      else
      {
         return n + m4(n+1);
      }
      
   }
   
   public static int m5(int n)
   {
      if(n == 1 || n == 0)
      {
         return 0;
      }   
      
      else
      {
         return n + m5(n-1) + m5(n-2);
      }
      
   }
   
   public static int m6(int n)
   {
      if(n == 1)
      {
         return 1;
      }   
      
      else
      {
         return n * m6(n-1);
      }
      
   }
   
   public static int m7(int a, int b)
   {
      if(a == 0)
      {
         return 0;
      }   
      
      else
      {
         return b + m7(a-1,b);
      }
      
   }
   
   public static int m8(int a, int b)
   {
      if(a == 0)
      {
         return 1;
      }   
      
      else
      {
         return b * m8(a-1,b);
      }
      
   }
   
   public static int m9(int a, int b)
   {
      if(b == 0)
      {
         return 1;
      }   
      
      else
      {
         return a * m9(a,b-1);
      }
      
   }
   
   public static int m10(int a, int b)
   {
      if(a<b)
      {
         return 5;
      }   
      
      else
      {
         return b * m9(a,b-1);
      }
      
   }







   
}