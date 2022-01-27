import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;


public class FerretTest {
	
	/*
	@Test
	public void testSortByWindow() {
		inputRegion[] testInput = new inputRegion[5];
		testInput[1] = new inputRegion("1",50, 100);
		testInput[2] = new inputRegion("2",500, 600);
		testInput[4] = new inputRegion("1",75, 150);
		testInput[3] = new inputRegion("1",200, 300);
		testInput[0] = new inputRegion("1",300, 350);
		ArrayList<inputRegion> sortedInput = Ferret.sortByWindow(testInput);
		assertTrue(sortedInput.get(0).getChr().equals("1"));
		assertTrue(sortedInput.get(0).getStart() == 50);
		assertTrue(sortedInput.get(0).getEnd() == 150);
		assertTrue(sortedInput.get(1).getChr().equals("1"));
		assertTrue(sortedInput.get(1).getStart() == 200);
		assertTrue(sortedInput.get(1).getEnd() == 350);
		assertTrue(sortedInput.get(2).getChr().equals("2"));
		assertTrue(sortedInput.get(2).getStart() == 500);
		assertTrue(sortedInput.get(2).getEnd() == 600);
	}
	 */
	/*
	@Test
	public void testGetGeneLGD(){
		assertNull(Ferret.getGeneLGD("test"));
	}
	
	@Test
	public void testNeedUpdate(){
		assertFalse(GUI.needUpdate());
	}
	
	@Test
	public void testInternetExample(){
		Ferret.internetExample();
	}
	
	@Test
	public void testGetQueryFromGeneName(){
		String[] test = new String[1];
		test[0] = "BDNF";
		//test[1] = "HCP5";
		//test[2] = "MYH9";
		inputRegion[] inputRegionList = GUI.getQueryFromGeneName(test);
		assertTrue(inputRegionList[0].getChr().equals("11"));
		assertEquals(inputRegionList[0].getStart(), 27676439);
		assertEquals(inputRegionList[0].getEnd(), 27743604);
	}
	*/
	/*
	@Test
	public void testGetQueryFromGeneID(){
		String[] test = new String[1];
		test[0] = "627";
		//test[1] = "HCP5";
		//test[2] = "MYH9";
		inputRegion[] inputRegionList = GUI.getQueryFromGeneID(test);
		assertTrue(inputRegionList[0].getChr().equals("11"));
		assertEquals(inputRegionList[0].getStart(), 27676439);
		assertEquals(inputRegionList[0].getEnd(), 27743604);
	}
	*/
	/*
	@Test
	public void testGetQueryFromSNPFrozenDb(){
		String[] test = new String[1];
		test[0] = "73885319";
		inputRegion[] blah = GUI.getQueryFromSNPFrozenDb(test);
	}
	
	@Test
	public void testCreateVCFFile(){
		inputRegion query = new inputRegion("3",10000000,10010000);
		ArrayList<inputRegion> listInputs = new ArrayList<inputRegion>();
		listInputs.add(query);
		
		assertEquals(Ferret.createVCFFile("/Users/PiTav/Desktop/Ferret test/basic test/blah", listInputs),253);
		
		//assertTrue(Ferret.createVCFTest("a","1").equals(Ferret.createVCFTest("a", "X")));
	}
		
	
	@Test
	public void testNeedUpdate(){
		assertTrue(GUI.needUpdate());
	}
	*/
	/*
	@Test
	public void testExomeSequencingProject(){
		inputRegion query = new inputRegion("22",36000000,37100000);
		ArrayList<inputRegion> listInputs = new ArrayList<inputRegion>();
		listInputs.add(query);
		LinkedList<espInfoObj> testLinkedList = FerretData.exomeSequencingProject(listInputs);
		System.out.println(testLinkedList);
		assertTrue(true);
	}
	*/
	@Test
	public void testtestPeopleOrder(){
		String testString = FerretData.getPeopleStringPhase1("1");
		assertTrue(FerretData.testPeopleOrder("Phase 1",testString));
		testString = FerretData.getPeopleStringPhase3("1");
		assertTrue(FerretData.testPeopleOrder("Phase 3",testString));
		assertTrue(FerretData.testPeopleOrder("Phase 3 GRCh38",testString));
	}
}
