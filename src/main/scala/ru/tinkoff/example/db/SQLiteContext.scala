package ru.tinkoff.example.db

import io.getquill.{SnakeCase, SqliteJdbcContext}

object SQLiteContext extends SqliteJdbcContext(SnakeCase, "ctx")
