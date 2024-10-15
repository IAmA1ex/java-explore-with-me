DROP TABLE IF EXISTS events_states CASCADE;
DROP TABLE IF EXISTS participants_statuses CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS participants CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilations_events CASCADE;

CREATE TABLE IF NOT EXISTS events_states (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(512) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS participants_statuses (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(512) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    initiator BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    annotation VARCHAR(255) NOT NULL,
    category BIGINT NOT NULL REFERENCES categories (id) ON DELETE CASCADE,
    description VARCHAR(512) UNIQUE NOT NULL,
    eventDate TIMESTAMP NOT NULL,
    createdOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    publishedOn TIMESTAMP,
    lat FLOAT,
    lon FLOAT,
    paid BOOLEAN NOT NULL,
    participantLimit BIGINT NOT NULL,
    requestModeration BOOLEAN NOT NULL,
    state BIGINT NOT NULL REFERENCES events_states (id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS participants (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    event BIGINT NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    requester BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status BIGINT NOT NULL REFERENCES participants_statuses (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    pinned BOOLEAN NOT NULL,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    compilation BIGINT NOT NULL REFERENCES compilations (id) ON DELETE CASCADE,
    event BIGINT NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    UNIQUE (compilation, event)
);



