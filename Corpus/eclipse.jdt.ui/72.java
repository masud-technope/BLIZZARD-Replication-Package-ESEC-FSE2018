//5, 23, 5, 33
package p;

class A {

    {
        boolean temp = this != null;
        do // LINE 2
        {
            boolean x = temp;
        } while (temp);
    }
}
