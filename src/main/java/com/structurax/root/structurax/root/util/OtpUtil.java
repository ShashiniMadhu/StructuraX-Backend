package com.structurax.root.structurax.root.util;

import java.util.Random;

public class OtpUtil {

    public static String generateOtp(){
            int otpLength = 8;

            Random random = new Random();

            StringBuilder otp = new StringBuilder(otpLength);

            for(int i=0 ;i<otpLength;i++){
                otp.append(random.nextInt(10));
            }
            return otp.toString();

        }
    }

