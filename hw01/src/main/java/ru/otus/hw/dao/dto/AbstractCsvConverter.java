package ru.otus.hw.dao.dto;

import com.opencsv.bean.AbstractBeanField;

public abstract class AbstractCsvConverter<T> extends AbstractBeanField<T, String> {
    public abstract Object convertToRead(String value);

    @Override
    protected Object convert(String value) {
        return convertToRead(value);
    }
}
