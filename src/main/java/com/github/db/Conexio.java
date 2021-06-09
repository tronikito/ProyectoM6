package com.github.db;

import com.github.exceptions.DatabaseException;

public interface Conexio {
    void connect() throws DatabaseException;
    void close() throws DatabaseException;
}
