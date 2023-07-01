package com.ledivax.util;

public class Paginator {

    public static Integer getFirstElement(Integer pageNumber, Long totalElements, Integer elementsPerPage) {
        Integer firstResult = (pageNumber - 1) * elementsPerPage;

        if (firstResult < 0) {
            firstResult = 0;
        } else if (firstResult > totalElements) {
            firstResult = Paginator.getLastPageNumber(Math.toIntExact(totalElements), elementsPerPage);
        }

        return firstResult;
    }

    public static Integer getLastPageNumber(Integer totalElements, Integer elementsPerPage) {
        Integer remainder = totalElements % elementsPerPage;
        if (remainder == 0) {
            return totalElements - elementsPerPage;
        } else {
            return totalElements - remainder;
        }
    }

    public static Integer limitingMinimumValueToOne(Integer num) {
        return num < 1 ? 1 : num;
    }
}
