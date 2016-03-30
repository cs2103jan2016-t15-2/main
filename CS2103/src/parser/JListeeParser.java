// @@author Her Kai Lin (A0126070U)

package parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joestelmach.natty.*;

import bean.Command;
import bean.CommandAddDeadlineTask;
import bean.CommandAddEvent;
import bean.CommandAddFloatTask;
import bean.CommandAddReserved;
import bean.CommandDelete;
import bean.CommandInvalid;
import bean.CommandRedo;
import bean.CommandShow;
import bean.CommandUndo;
import bean.CommandUpdate;

public class JListeeParser {
	private static final String CONTAINING_END = "end";
	private static final String CONTAINING_START = "start";
	private static final int STARTING_INDEX = 0;
	private static final String CONTAINS_COMMA = ",";
	private static final String CONTAINS_ALL = "all";
	private static final String COMMAND_SHOW = "show";
	private static final String COMMAND_REDO = "redo";
	private static final String COMMAND_UNDO = "undo";
	private static final String COMMAND_UPDATE = "update";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_RESERVE = "reserve";

	private static final int DEFAULT_START_HOUR = 0;
	private static final int DEFAULT_START_MINUTE = 0;
	private static final int DEFAULT_START_SECOND = 0;
	private static final int DEFAULT_START_MILLISECOND = 0;
	private static final int DEFAULT_END_HOUR = 23;
	private static final int DEFAULT_END_MINUTE = 59;
	private static final int DEFAULT_END_SECOND = 0;
	private static final int DEFAULT_END_MILLISECOND = 0;

	private static Logger logger = Logger.getGlobal();
	private static FileHandler fh;
	private com.joestelmach.natty.Parser dateParser;

	private Calendar startDate = null;
	private Calendar endDate = null; 
	
	public JListeeParser() {
		dateParser = new com.joestelmach.natty.Parser();
	}

<<<<<<< HEAD


 public static void main(String[] separateInputLine){ // for testing
		try {
			fh = new FileHandler("log\\log.txt");
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  

		} catch (SecurityException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		} 


		JListeeParser testEvent = new JListeeParser();
		testEvent.ParseCommand("add event  (03/17/2016 2pm to 03/27/2016 5pm) add event @LT27 #hihi #me");

		JListeeParser testFloat = new JListeeParser();
		testFloat.ParseCommand("add floatingtask @zzz #arghhhhh #hi");

		JListeeParser testDeadLine = new JListeeParser();
		testDeadLine.ParseCommand("add deadLine date only @whatthe 12/2/14 #hey");

		JListeeParser testDeadLine2 = new JListeeParser();
		testDeadLine2.ParseCommand("add deadLine date and time @location today 3:00 #hashtag");

		JListeeParser testDelete = new JListeeParser();
		testDelete.ParseCommand("delete 1,2,3,4"); 
		
		JListeeParser testShow = new JListeeParser();
		testShow.ParseCommand("show hiiiiii 12/4/16 and today #hihi @location ");
		
		JListeeParser testReserve = new JListeeParser();
		testReserve.ParseCommand("reserve r1 tomorrow 3pm - 5pm and 7pm - 8pm"); 

		JListeeParser testUpdateTask = new JListeeParser();
		testUpdateTask.ParseCommand("update 2 today and april 29 #hashtag -#sigh @icube "); 
	} 




	
		/*
=======
	/*
>>>>>>> ab7b94527077da5bf3e8dca7bcefe94e51c4f08a
	 * public static void main(String[] separateInputLine){ // for testing try {
	 * fh = new FileHandler(
	 * "/Users/kailin/Desktop/IVLE/CS2103/for proj/cs2103 proj/CS2103/src/MyLogFile.log"
	 * ); logger.addHandler(fh); SimpleFormatter formatter = new
	 * SimpleFormatter(); fh.setFormatter(formatter);
	 * 
	 * } catch (SecurityException e) { e.printStackTrace(); } catch (IOException
	 * e) { e.printStackTrace(); }
	 * 
	 * 
	 * JListeeParser testEvent = new JListeeParser(); testEvent.ParseCommand(
	 * "add event  (03/17/2016 2pm to 03/27/2016 5pm) add event @LT27 #hihi #me"
	 * );
	 * 
	 * 
	 * JListeeParser testFloat = new JListeeParser(); testFloat.ParseCommand(
	 * "add floatingtask @zzz #arghhhhh #hi");
	 * 
	 * JListeeParser testDeadLine = new JListeeParser();
	 * testDeadLine.ParseCommand("add deadLine date only @whatthe 12/2/14 #hey"
	 * );
	 * 
	 * JListeeParser testDeadLine2 = new JListeeParser();
	 * testDeadLine2.ParseCommand(
	 * "add deadLine date and time @location today 3:00 #hashtag");
	 * 
	 * JListeeParser testDelete = new JListeeParser(); testDelete.ParseCommand(
	 * "delete 1,2,3,4");
	 * 
	 * JListeeParser testShow = new JListeeParser(); testShow.ParseCommand(
	 * "show hiiiiii 23rd march 2016 to 12/4/16 #hihi @location ");
	 * 
	 * JListeeParser testReserve = new JListeeParser();
	 * testReserve.ParseCommand("reserve r1 tomorrow 3pm - 5pm and 7pm - 8pm");
	 * 
	 * JListeeParser testUpdateTask = new JListeeParser();
	 * testUpdateTask.ParseCommand(
	 * "update 2 what -tomorrow  @location #hashtag -#deleteHashtag -#sigh #hi "
	 * ); }
	 */

	public Command ParseCommand(String inputLine) {

		String[] separateInputLine = inputLine.split(" ");
		String commandType = determineCommandType(separateInputLine);

		switch (commandType) {
		case COMMAND_ADD:
			return parseAdd(inputLine);

		case COMMAND_DELETE:
			return parseDelete(inputLine);

		case COMMAND_UNDO:
			return parseUndo();

		case COMMAND_REDO:
			return parseRedo();

		case COMMAND_SHOW:
			return parseShow(inputLine);

		case COMMAND_RESERVE:
			return parseReserve(inputLine);

		case COMMAND_UPDATE:
			return parseUpdate(inputLine);

		default:
			return parseInvalid();
		}
	}

	public Command parseAdd(String inputLine) {
		boolean isEvent = false;
		boolean isDeadline = false;
		String location = null;
		ArrayList<String> tagLists = new ArrayList<String>();
	
		inputLine = inputLine.replaceFirst(COMMAND_ADD, "").trim();
	
		// natty library to extract dates
		List<DateGroup> groups = dateParser.parse(inputLine);
	
		for (DateGroup group : groups) {
			List<Date> dates = group.getDates();
	
			/* has start date and end date, event task */
	
			if (dates.size() == 2) {
				isEvent = true;
				inputLine = extractDateAndInputLine(inputLine, groups);
				
			}
	
			/* only 1 set of date, deadlineTask */
			else if (dates.size() == 1) {
				isDeadline = true;
				inputLine = extractDateAndInputLine(inputLine, groups);

			}
	
		}
	
		tagLists = findHashTags(inputLine);
	
		location = findLocation(inputLine);
	
		String taskDescription = trimInputLineToDescriptionOnly(inputLine, location, tagLists);
	
			
		if (isEvent) {
			return new CommandAddEvent(taskDescription, location, startDate, endDate, tagLists);
		}
	
		else if (isDeadline) {
			return new CommandAddDeadlineTask(taskDescription, location, endDate, tagLists);
		}
	
		
		return new CommandAddFloatTask(taskDescription, location, tagLists);
	}

	public Command parseDelete(String inputLine) {
		ArrayList<Integer> taskNumbers = new ArrayList<Integer>();
		inputLine = inputLine.replace(COMMAND_DELETE, "").trim();
	
		if (inputLine.contains(CONTAINS_ALL)) {
			taskNumbers = null;
		}
	
		else {
			String[] separateInputLine = inputLine.split(CONTAINS_COMMA);
			for (int startIndex = STARTING_INDEX; startIndex < separateInputLine.length; startIndex++) {
				taskNumbers.add(Integer.valueOf(separateInputLine[startIndex]));
			}
		}
	
		return new CommandDelete(taskNumbers);
	}


	public Command parseUndo() {
		return new CommandUndo();
	}

	public Command parseRedo() {
		return new CommandRedo();
	}

	private Command parseShow(String inputLine) {
		String location = null;
		ArrayList<String> tagLists = new ArrayList<String>();
		String taskDescription = null;
	
		inputLine = inputLine.replaceFirst(COMMAND_SHOW, "").trim();
		
		// natty library to extract dates
		List<DateGroup> groups = dateParser.parse(inputLine);
	
		inputLine = extractDateAndInputLine(inputLine, groups);
	
		tagLists = findHashTags(inputLine);
	
		location = findLocation(inputLine);
	
		if (!inputLine.contains(CONTAINS_ALL)) {
			taskDescription = trimInputLineToDescriptionOnly(inputLine, location, tagLists);
		}
<<<<<<< HEAD
		
		if (location == null && startDate == null && endDate == null && tagLists == null){
			return new CommandShow(taskDescription);
		}
		
		return new CommandShow(taskDescription, location, startDate, endDate, tagLists);
=======
		return new CommandShow(taskDescription);

>>>>>>> ab7b94527077da5bf3e8dca7bcefe94e51c4f08a
	}


	public Command parseReserve(String inputLine) {
		
		String location = null;
		ArrayList<String> tagLists = new ArrayList<String>();
		ArrayList<Calendar> startDates = new ArrayList<Calendar>();
		ArrayList<Calendar> endDates = new ArrayList<Calendar>();
	
		inputLine = inputLine.replaceFirst(COMMAND_RESERVE, "").trim();
	
		// natty library to extract dates
		List<DateGroup> groups = dateParser.parse(inputLine);
	
		for (DateGroup group : groups) {
			List<Date> dates = group.getDates();
			/* has start date and end date, event task */
	
			if (dates.size() >= 2) {
				extractDates(startDates, endDates, group, dates);
			}
	
			inputLine = removeDateFromInputLine(inputLine, group);
		}
	
		tagLists = findHashTags(inputLine);
	
		location = findLocation(inputLine);
	
		String taskDescription = trimInputLineToDescriptionOnly(inputLine, location, tagLists);
		return new CommandAddReserved(taskDescription, location, startDates, endDates, tagLists);
	
	}

	public Command parseUpdate(String inputLine) {
		String location = null;
		String taskDescription = null;
		ArrayList<String> removeTagLists = new ArrayList<String>();
		ArrayList<String> tagLists = new ArrayList<String>();
		Integer taskNumber;
<<<<<<< HEAD
	
		inputLine = inputLine.replaceFirst(COMMAND_UPDATE, "").trim();
=======
		System.out.println(inputLine);
		inputLine = inputLine.replaceFirst("update", "").trim();
>>>>>>> ab7b94527077da5bf3e8dca7bcefe94e51c4f08a
		taskNumber = extractTaskNumber(inputLine);
	
		if (inputLine.contains(String.valueOf(taskNumber))) {
			inputLine = inputLine.replace(String.valueOf(taskNumber), "").trim();
		}
<<<<<<< HEAD
		
		//change event task start date
		if (inputLine.contains(CONTAINING_START)) {
			inputLine = inputLine.replaceFirst(CONTAINING_START, "").trim();
	
			List<DateGroup> groups = dateParser.parse(inputLine);
	
			inputLine = extractStartTimeAndInputLine(inputLine, groups);
			
		}
		
		//change event task end date
		else if (inputLine.contains(CONTAINING_END)){
			inputLine = inputLine.replaceFirst(CONTAINING_END, "").trim();
	
			List<DateGroup> groups = dateParser.parse(inputLine);
	
			inputLine = extractEndTimeAndInputLine(inputLine, groups);
			
		}
		
		//change event task start & end date
		// or change deadline end date
		else {
			List<DateGroup> groups = dateParser.parse(inputLine);
			inputLine = extractDateAndInputLine(inputLine, groups);
			
		}
		
		if (inputLine.contains("-")){
=======
		// natty library to extract dates
		if (inputLine.contains("-")) {

			List<DateGroup> groups = dateParser.parse(inputLine);

			for (DateGroup group : groups) {
				List<Date> dates = group.getDates();

				if (dates.size() == 2) {
					for (int i = 0; i < dates.size() - 1; i += 2) {
						startDate = dateToCalendar(dates.get(i));
						startDate.setTimeInMillis(0);
						endDate = dateToCalendar(dates.get(i + 1));
						endDate.setTimeInMillis(0);

						// swap dates if start after end date
						if (startDate.after(endDate)) {
							Calendar temp = endDate;
							endDate = startDate;
							startDate = temp;
						}

					}

				}

				else if (dates.size() == 1) {
					endDate = dateToCalendar(dates.get(0));
					endDate.setTimeInMillis(0);
				}

				inputLine = removeRemoveDateFromInputLine(inputLine, group);

			}

>>>>>>> ab7b94527077da5bf3e8dca7bcefe94e51c4f08a
			removeTagLists = findHashTags(inputLine);
			inputLine = trimInputLineWithoutRemoveHashTags(inputLine, removeTagLists);

		}

		List<DateGroup> groups = dateParser.parse(inputLine);

		for (DateGroup group : groups) {
			List<Date> dates = group.getDates();
			/* has start date and end date, event task */

			if (dates.size() == 2) {
				for (int i = 0; i < dates.size() - 1; i += 2) {
					startDate = dateToCalendar(dates.get(i));
					endDate = dateToCalendar(dates.get(i + 1));

					/* Swap date if necessary */
					if (startDate.after(endDate)) {
						Calendar temp = endDate;
						endDate = startDate;
						startDate = temp;
					}
				}

				if (group.isTimeInferred()) {
					setStartDateTimeDefault(startDate);
					setEndDateTimeDefault(endDate);
				}
			}

			else if (dates.size() == 1) {
				endDate = dateToCalendar(dates.get(0));

				/* set default end date if no time specified */
				if (group.isTimeInferred()) {
					setEndDateTimeDefault(endDate);
				}
			}

			inputLine = removeDateFromInputLine(inputLine, group).trim();
		}

		tagLists = findHashTags(inputLine);
		location = findLocation(inputLine);
	
		taskDescription = trimInputLineToDescriptionOnly(inputLine, location, tagLists);
<<<<<<< HEAD
		
		if (taskDescription.equals("")){
			taskDescription = null;
		}
		
		

=======
>>>>>>> ab7b94527077da5bf3e8dca7bcefe94e51c4f08a
		return new CommandUpdate(taskNumber, taskDescription, location, startDate, endDate, tagLists, removeTagLists);
	}

<<<<<<< HEAD
=======
	private String removeRemoveDateFromInputLine(String inputLine, DateGroup group) {
		if (inputLine.contains(group.getText())) {
			inputLine = inputLine.replace("-" + group.getText(), "");
		}
		return inputLine;
	}

	public String trimInputLineWithoutRemoveHashTags(String inputLine, ArrayList<String> removeTagLists) {
>>>>>>> ab7b94527077da5bf3e8dca7bcefe94e51c4f08a


	public Command parseInvalid() {
		return new CommandInvalid();
	}

	public String extractEndTimeAndInputLine(String inputLine, List<DateGroup> groups) {
		for (DateGroup group : groups) {
			List<Date> dates = group.getDates();
			endDateToCalendar(dates.get(0));	
	
			if (group.isTimeInferred()) {
				setEndDateTimeDefault();
			}
			inputLine = removeRemoveDateFromInputLine(inputLine, group);
	
		}
		return inputLine;
	}



	public String extractStartTimeAndInputLine(String inputLine, List<DateGroup> groups) {
		for (DateGroup group : groups) {
			List<Date> dates = group.getDates();
			startDateToCalendar(dates.get(0));
	
			if (group.isTimeInferred()) {
				setStartDateTimeDefault();
			}
			
			inputLine = removeRemoveDateFromInputLine(inputLine, group);
				
		}
<<<<<<< HEAD
		return inputLine;
=======

		tagLists = findHashTags(inputLine);

		location = findLocation(inputLine);

		String taskDescription = trimInputLineToDescriptionOnly(inputLine, location, tagLists);

		return new CommandAddReserved(taskDescription, location, startDates, endDates, tagLists);

>>>>>>> ab7b94527077da5bf3e8dca7bcefe94e51c4f08a
	}


	public void extractDates(ArrayList<Calendar> startDates, ArrayList<Calendar> endDates, DateGroup group,
			List<Date> dates) {
		for (int i = STARTING_INDEX; i < dates.size() - 1; i += 2) {
			startDateToCalendar(dates.get(i));
			endDateToCalendar(dates.get(i + 1));
			
			swapDates();
			
			startDates.add(startDate);
			endDates.add(endDate);
		}
	
		if (group.isTimeInferred()) {
			setStartDateTimeDefault();
			setEndDateTimeDefault();
		}
	}



	public String extractDateAndInputLine(String inputLine, List<DateGroup> groups) {
		for (DateGroup group : groups) {
			List<Date> dates = group.getDates();
	
			/* has start date and end date, search inbetween 2 dates */
			if (dates.size() == 2) {
				setStartEndTime(group, dates);
			}
	
			/* only 1 set of date, search for that specific dates */
			else if (dates.size() == 1) {
				setEndTime(group, dates);
			}
	
			inputLine = removeDateFromInputLine(inputLine, group);
		}
		return inputLine;
	}



	public Integer extractTaskNumber(String inputLine) {
		String[] splitInputLine = inputLine.split(" ");
		return Integer.valueOf(splitInputLine[0]);
	}



	public void setEndTime(DateGroup group, List<Date> dates) {
		endDateToCalendar(dates.get(0));
		
		/* set default end time if no time specified */
		if (group.isTimeInferred()) {
			setEndDateTimeDefault();
		}
	}


	public void setStartEndTime(DateGroup group, List<Date> dates) {
		startDateToCalendar(dates.get(0));
		endDateToCalendar(dates.get(1));

		swapDates();
		
		/* set default start end time if no time specified */
		if (group.isTimeInferred()) {
			setStartDateTimeDefault();
			setEndDateTimeDefault();
		}
	}

	private String removeRemoveDateFromInputLine(String inputLine, DateGroup group) {
		if (inputLine.contains(group.getText())) {
			inputLine = inputLine.replaceFirst(group.getText(), "");
		}
		return inputLine;
	}

	public String trimInputLineWithoutRemoveHashTags(String inputLine, ArrayList<String> removeTagLists) {

		for (int i = 0; i < removeTagLists.size(); i++) {
			inputLine = inputLine.replace("-#" + removeTagLists.get(i), "").trim();
		}

		return inputLine;
	}

	public String removeDateFromInputLine(String inputLine, DateGroup group) {
		if (inputLine.contains(group.getText())) {
			inputLine = inputLine.replace(group.getText(), "");
		}
		return inputLine;
	}

	public String trimInputLineToDescriptionOnly(String inputLine, String location, ArrayList<String> tagLists) {
		if (tagLists != null) {
			for (int i = 0; i < tagLists.size(); i++) {
				inputLine = inputLine.replace("#" + tagLists.get(i), "").trim();
			}
		}

		inputLine = inputLine.replace("@" + location, "").trim();
		inputLine = inputLine.replace("(", "").replace(")", "").replace("@", "");

		return inputLine;
	}

	public String findLocation(String inputLine) {
		String location = null;
		Matcher retrieveLocation = Pattern.compile("@\\s*(\\w+)").matcher(inputLine);
		while (retrieveLocation.find()) {
			location = retrieveLocation.group(1);
		}
		return location;
	}

	public ArrayList<String> findHashTags(String inputLine) {
		ArrayList<String> tags = new ArrayList<String>();
		Matcher retrieveHashTags;

		if (inputLine.contains("-")) {
			retrieveHashTags = Pattern.compile("-#\\s*(\\w+)").matcher(inputLine);
			while (retrieveHashTags.find()) {
				tags.add(retrieveHashTags.group(1));
			}

		}

		else {
			retrieveHashTags = Pattern.compile("#\\s*(\\w+)").matcher(inputLine);

			while (retrieveHashTags.find()) {
				tags.add(retrieveHashTags.group(1));
			}
		}

		return tags;
	}

	public void setEndDateTimeDefault() {
		endDate.set(Calendar.HOUR_OF_DAY, DEFAULT_END_HOUR);
		endDate.set(Calendar.MINUTE, DEFAULT_END_MINUTE);
		endDate.set(Calendar.SECOND, DEFAULT_END_SECOND);
		endDate.set(Calendar.MILLISECOND, DEFAULT_END_MILLISECOND);
	}

	public void setStartDateTimeDefault() {
		startDate.set(Calendar.HOUR_OF_DAY, DEFAULT_START_HOUR);
		startDate.set(Calendar.MINUTE, DEFAULT_START_MINUTE);
		startDate.set(Calendar.SECOND, DEFAULT_START_SECOND);
		startDate.set(Calendar.MILLISECOND, DEFAULT_START_MILLISECOND);
	}

	public String determineCommandType(String[] separateInputLine) {
		return separateInputLine[0].trim();
	}

	private void startDateToCalendar(Date date) {
		startDate = Calendar.getInstance();
		startDate.setTime(date);
	}
	
	private void endDateToCalendar(Date date) {
		endDate = Calendar.getInstance();
		endDate.setTime(date);
	}

	public void swapDates(){
		if (startDate.after(endDate)) {
			Calendar temp = endDate;
			endDate = startDate;
			startDate = temp;
		}
	}
	
}
