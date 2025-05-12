package manager;

import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final TaskLinkedList history = new TaskLinkedList();

    @Override
    public void add(Task task) {
        history.linkLast(task);
    }

    @Override
    public void remove(int id) {
        history.removeNode(history.getNode(id));
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    public static class TaskLinkedList {
        private final Map<Integer, Node> historyIdMap = new HashMap<>();
        private Node head;
        private Node tail;

        // получение узла по идентификатору
        private Node getNode(int id) {
            return historyIdMap.get(id);
        }

        private void linkLast(Task task) {
            Node newNode = new Node();
            newNode.setTask(task);

            if (historyIdMap.containsKey(task.getId())) {
                removeNode(historyIdMap.get(task.getId()));
            }

            // добавление узла
            if (head == null) {
                tail = newNode; // присваивание хвосту узел
                head = newNode; // присваивание голове узел
                newNode.setNext(null); // ссылка на следующий узел пустая
                newNode.setPrev(null); // ссылка на предыдущий узел пустая
            } else {
                newNode.setPrev(tail); // меняется ссылка на предыдущий узел
                newNode.setNext(null); // ссылка на следующий узел пустая
                tail.setNext(newNode); // меняется ссылка хвоста на новый узел
                tail = newNode; // обновление хвоста
            }

            historyIdMap.put(task.getId(), newNode);
        }

        public List<Task> getTasks() {
            List<Task> taskList = new ArrayList<>();
            Node nodePart = head;

            while (nodePart != null) {
                taskList.add(nodePart.getTask());
                nodePart = nodePart.getNext();
            }

            return taskList;
        }

        private void removeNode(Node node) {
            if (node != null) {
                historyIdMap.remove(node.getTask().getId());
                Node prev = node.getPrev();
                Node next = node.getNext();

                if (tail == node) {
                    tail = node.getPrev();
                }
                if (head == node) {
                    head = node.getNext();
                }
                if (prev != null) {
                    prev.setNext(next);
                }
                if (next != null) {
                    next.setPrev(prev);
                }
            }
        }
    }

    static class Node {
        private Task task;
        private Node prev;
        private Node next;

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }
}



