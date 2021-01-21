package com.qsoftware.qos.testapp;

public class Main {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();

        if (args.length == 0) {
            System.out.println();
            return;
        }
        sb.append(args[0]);
        for (int i = 1; i < args.length; i++) {
            sb.append(" ");
            sb.append(args[i]);
        }

        System.out.println(sb);
    }
}
