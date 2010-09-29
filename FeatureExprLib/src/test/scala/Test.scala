import org.junit.Assert._
import org.junit.Test;

class AdditionTest {

  val x = 1;
  val y = 1;
  
  @Test def addition() {
    val z = x + y;
    assertEquals(2, z);
  }

}
