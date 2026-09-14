package com.abhinav.rate_limiter;

import org.apache.el.parser.Token;

public class TokenBucketTest {
    public static void main(String[] args) {
        TokenBucket bucket = new TokenBucket(3, 0.77);
        for(int i = 1; i<=60; i++)
        {
            System.out.println(bucket.tryConsume());
            try{
                Thread.sleep(2000);
            }
            catch(Exception e)
            {

            }
        }
    }
}
