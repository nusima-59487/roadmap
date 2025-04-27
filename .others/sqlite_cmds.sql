-- Active: 1742607474664@@127.0.0.1@3306




--- Initial SQL commands for the database
--- 1: create nodes table
CREATE TABLE IF NOT EXISTS Nodes (
    node_id INTEGER PRIMARY KEY AUTOINCREMENT,
    coord_x INTEGER NOT NULL, 
    coord_z INTEGER NOT NULL,
    name TEXT,
    aux_node BOOLEAN NOT NULL DEFAULT 0, -- Auxiliary node
    has_stop_sign BOOLEAN, -- Has stop sign (if not aux)
    has_traffic_light BOOLEAN, -- Has traffic light (if not aux)
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_update_user_uuid TEXT NOT NULL,
    last_update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--- 2: create segments table
CREATE TABLE IF NOT EXISTS Segments (
    segment_id INTEGER PRIMARY KEY AUTOINCREMENT,
    starting_node INTEGER NOT NULL,
    ending_node INTEGER NOT NULL,
    is_one_way BOOLEAN NOT NULL DEFAULT 0, 
    lane_count_forward INTEGER NOT NULL DEFAULT 2,
    lane_count_backward INTEGER DEFAULT 2,
    speed_limit INTEGER NOT NULL DEFAULT 50,
    road_type TEXT NOT NULL DEFAULT "Road",
    name TEXT,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_update_user_uuid TEXT NOT NULL,
    last_update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (starting_node) REFERENCES Nodes(node_id)
        ON DELETE CASCADE,
    FOREIGN KEY (ending_node) REFERENCES Nodes(node_id)
        ON DELETE CASCADE
);

--- 3: create segment and aux node relations table
CREATE TABLE SegmentAlignments (
    segment_id INTEGER NOT NULL, 
    aux_node_id INTEGER NOT NULL,
    alignment_index INTEGER NOT NULL,
    lod_level INTEGER NOT NULL DEFAULT 1, -- smaller number = loaded when zoomed in smaller
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_update_user_uuid TEXT NOT NULL,
    last_update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (segment_id) REFERENCES Segments(segment_id)
        ON DELETE CASCADE,
    FOREIGN KEY (aux_node_id) REFERENCES Nodes(node_id)
        ON DELETE CASCADE,
    PRIMARY KEY (segment_id, alignment_index)
);

--- 4: create main node connections table
CREATE TABLE NodeConnections (
    node_id INTEGER NOT NULL,
    direction TEXT NOT NULL DEFAULT 'none', -- SQLite does not support ENUM
    segment_id INTEGER NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_update_user_uuid TEXT NOT NULL,
    last_update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (node_id) REFERENCES Nodes(node_id)
        ON DELETE CASCADE,
    FOREIGN KEY (segment_id) REFERENCES Segments(segment_id)
        ON DELETE CASCADE,
    PRIMARY KEY (node_id, direction)
);


-- SQLite does not support stored procedures. Convert this logic to application code.


-- TEST
INSERT INTO Nodes (coord_x, coord_z, aux_node, last_update_user_uuid) VALUES 
    (0, 0, 0, "console"), 
    (0, 10, 0, "console"), 
    (0, -10, 0, "console"), 
    (7, 15, 1, "console"), 
    (10, 15, 0, "console"), 
    (10, 0, 0, "console"), 
    (13, 0, 1, "console"), 
    (20, 15, 1, "console"),
    (20, 7, 0, "console"), 
    (20, -10, 0, "console");

-- SQLite does not support variables like @ids. Use application code to handle this logic.
-- Example query:
SELECT node_id FROM Nodes WHERE coord_x = 10 AND coord_z = 15;

INSERT INTO Segments (starting_node, ending_node, last_update_user_uuid) VALUES 
    (1, 2, "4c15b77c-7a44-46c7-953b-ff0d1c67a653");