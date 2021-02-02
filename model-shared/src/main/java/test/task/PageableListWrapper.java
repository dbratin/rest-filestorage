package test.task;

import java.util.List;

public class PageableListWrapper<T> {
    private final List<T> list;
    private final int currentPage;
    private final int pagesCount;

    public PageableListWrapper(List<T> list, int currentPage, int pagesCount) {
        this.list = list;
        this.currentPage = currentPage;
        this.pagesCount = pagesCount;
    }

    public List<T> getList() {
        return list;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPagesCount() {
        return pagesCount;
    }
}
