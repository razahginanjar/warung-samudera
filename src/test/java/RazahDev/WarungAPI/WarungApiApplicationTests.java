package RazahDev.WarungAPI;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WarungApiApplicationTests {

	public enum TipeTransaksi{
		ONLINE,
		TAKE_AWAY,
		EAT_IN
	}
	@Test
	void contextLoads() {
		TipeTransaksi tipeTransaksi = TipeTransaksi.TAKE_AWAY;
		System.out.println(tipeTransaksi.toString().replace("_", " "));
	}

}
