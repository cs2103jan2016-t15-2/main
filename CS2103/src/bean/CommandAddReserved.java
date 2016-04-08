/*
 * @@author A0139995E
 */
package bean;

import java.util.ArrayList;
import java.util.Calendar;

public class CommandAddReserved implements Command {
    private TaskReserved _task;
    private boolean _updateFile = true;
    private boolean _saveHistory = true;
    private ArrayList<Integer> _conflictingTasksIndices = new ArrayList<Integer>();

    public CommandAddReserved() {
        _task = null;
    }

    public CommandAddReserved(TaskReserved task) {
        this._task = task;
    }

    public CommandAddReserved(String description, String location, ArrayList<Calendar> startDates,
            ArrayList<Calendar> endDates, ArrayList<String> tags) {
        _task = new TaskReserved(description, location, startDates, endDates, tags);
    }

    public Display execute(Display display) {
        if (hasNoDescription()) {
            setInvalidDisplay(display, GlobalConstants.MESSAGE_ERROR_DESCRIPTION);
            return display;
        }
        if (containsInvalidTimeSlots()) {
            setInvalidDisplay(display, GlobalConstants.MESSAGE_ERROR_TIME_RANGE);
            return display;
        }
        display.getReservedTasks().add(_task);
        if (!display.getVisibleReservedTasks().equals(display.getReservedTasks())) {
            display.getVisibleReservedTasks().add(_task);
        }
        setDisplay(display);
        return display;
    }

    private boolean hasNoDescription() {
        if (_task.getDescription() == null) {
            return true;
        } else {
            _task.setDescription(_task.getDescription().trim());
            if (_task.getDescription().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void setInvalidDisplay(Display display, String msg) {
        _updateFile = false;
        _saveHistory = false;
        display.setCommandType(GlobalConstants.GUI_ANIMATION_INVALID);
        display.setMessage(msg);
    }

    private void setDisplay(Display display) {
        ArrayList<Integer> taskIndices = new ArrayList<Integer>();
        display.setCommandType(GlobalConstants.GUI_ANIMATION_ADD);
        int index = display.getVisibleReservedTasks().indexOf(_task)
                + display.getVisibleDeadlineTasks().size() + display.getVisibleEvents().size()
                + display.getVisibleFloatTasks().size() + 1;
        taskIndices.add(index);
        display.setTaskIndices(taskIndices);
        display.setMessage(String.format(GlobalConstants.MESSAGE_RESERVED, _task.getDescription()));
        getConflictingTasks(display);
    }

    private void getConflictingTasks(Display display) {
        getConflictingEvents(display);
        getConflictingReservedTasks(display);
        display.setConflictingTasksIndices(_conflictingTasksIndices);
    }

    private void getConflictingReservedTasks(Display display) {
        ArrayList<TaskReserved> listReserved = display.getReservedTasks();
        for (TaskReserved myTask : listReserved) {
            checkReservedTask: for (int i = 0; i < myTask.getStartDates().size(); i++) {
                for (int j = 0; j < _task.getStartDates().size(); j++) {
                    if (!myTask.equals(_task)) {
                        if (isWithinTimeRange(_task.getStartDates().get(j), _task.getEndDates().get(j),
                                myTask.getStartDates().get(i), myTask.getEndDates().get(i))) {
                            int index = display.getVisibleReservedTasks().indexOf(myTask);
                            if (isValidIndex(index)) {
                                index = getConflictingTaskReservedIndex(display, index);
                                _conflictingTasksIndices.add(index);
                            }
                            break checkReservedTask;
                        }
                    }
                }
            }
        }
    }

    private int getConflictingTaskReservedIndex(Display display, int index) {
        return index + display.getVisibleDeadlineTasks().size() + display.getVisibleEvents().size()
                + display.getVisibleFloatTasks().size() + 1;
    }

    private boolean isValidIndex(int index) {
        return index >= 0;
    }

    private void getConflictingEvents(Display display) {
        ArrayList<TaskEvent> listEvents = display.getEventTasks();
        for (TaskEvent myTask : listEvents) {
            for (int i = 0; i < _task.getStartDates().size(); i++) {
                if (isWithinTimeRange(_task.getStartDates().get(i), _task.getEndDates().get(i),
                        myTask.getStartDate(), myTask.getEndDate())) {
                    int index = display.getVisibleEvents().indexOf(myTask);
                    if (isValidIndex(index)) {
                        index = getConflictingTaskEventIndex(display, index);
                        _conflictingTasksIndices.add(index);
                    }
                    break;
                }
            }
        }
    }

    private int getConflictingTaskEventIndex(Display display, int index) {
        return index + display.getVisibleDeadlineTasks().size() + 1;
    }

    private boolean isWithinTimeRange(Calendar start, Calendar end, Calendar rangeStart, Calendar rangeEnd) {
        if (!start.before(rangeStart)) {
            if (!start.after(rangeEnd)) {
                return true;
            }
        } else if (!end.before(rangeStart)) {
            return true;
        }
        return false;
    }
    
    private boolean containsInvalidTimeSlots() {
        if (_task.getStartDates() == null) {
            return true;
        } else if (_task.getEndDates() == null) {
            return true;
        }
        if(_task.getStartDates().size() != _task.getEndDates().size()){
            return true;
        }
        if((_task.getStartDates().isEmpty()) || (_task.getEndDates().isEmpty())){
            return true;
        }
        for (int i = 0; i < _task.getStartDates().size(); i++) {
            if (_task.getStartDates().get(i).after(_task.getEndDates().get(i))) {
                return true;
            }
        }
        return false;
    }

    /*
     * private ArrayList<TaskReserved> addReservedTask(ArrayList<TaskReserved>
     * taskList) { int index = getIndex(taskList); taskList.add(index, task);
     * return taskList; }
     */

    /*
     * This method searches for the index to slot the deadline task in since we
     * are sorting the list in order of earliest start date of the first time
     * slot.
     *//*
       * private int getIndex(ArrayList<TaskReserved> taskList) { int i = 0;
       * Calendar addedTaskStartDate = task.getStartDates().get(0); for (i = 0;
       * i < taskList.size(); i++) { Calendar taskInListStartDate =
       * taskList.get(i).getStartDates().get(0); if
       * (addedTaskStartDate.compareTo(taskInListStartDate) <= 0) { break; } }
       * return i; }
       */

    public boolean requiresSaveHistory() {
        return _saveHistory;
    }

    public boolean requiresUpdateFile() {
        return _updateFile;
    }
}
