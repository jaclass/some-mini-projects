package scalashop

import java.util.concurrent._
import scala.collection._
import org.junit._
import org.junit.Assert.assertEquals

class BlurSuite {
  val miniImg = new Img(4, 4, Array(0x0000ff00,0x0000ff00,0x0000ff00,0x0000ff00,0x0000ff00,0x0000ff00,0x0000ff00,0x0000ff00,0x0000ff00,0x0000ff00,0x0000ff00,0x0000ff00,0x0000ff00,0x0000ff00,0x0000ff00,0x0000ff00))
  @Test def `boxBlurKernel Test 1`: Unit =
    assertEquals(0x0000ff00, boxBlurKernel(miniImg, 2, 2, 1))
  @Test def `boxBlurKernel Test 2`: Unit =
    assertEquals(0x0000ff00, boxBlurKernel(miniImg, 0, 0, 1))
  @Test def `boxBlurKernel Test 3`: Unit =
    assertEquals(0x0000ff00, boxBlurKernel(miniImg, 2, 2, 2))
  @Test def `boxBlurKernel Test 4`: Unit =
    assertEquals(0x0000ff00, boxBlurKernel(miniImg, 3, 3, 1))
  @Rule def individualTestTimeout = new org.junit.rules.Timeout(10 * 1000)
}
