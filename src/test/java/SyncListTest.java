import org.junit.Test;

import java.util.ArrayList;

public class SyncListTest {

  @Test
  public void testReplaceElement() {

    ArrayList<Integer> l = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      l.add(i);
    }
    System.out.println(l.toString());
    l.set(3,null);
    l.set(5,null);
    System.out.println(l.toString() + l.size());

  }

}
