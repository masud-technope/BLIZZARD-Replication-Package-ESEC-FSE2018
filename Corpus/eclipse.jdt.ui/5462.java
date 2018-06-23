//5, 23, 5, 33
package p;

class A {

    {
        do // LINE 2
        {
            boolean x = this != null;
        } while (this != null);
    }
}
