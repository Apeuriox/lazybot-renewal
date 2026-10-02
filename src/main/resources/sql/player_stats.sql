CREATE TABLE IF NOT EXISTS player_stats_daily_template (
  id INT NOT NULL,
  mode TINYINT NOT NULL,
  subserver TINYINT NOT NULL,
  record_date_time DATETIME NOT NULL,
  player_name VARCHAR(128) NOT NULL,
  performance_point DOUBLE NULL,
  global_rank INT NULL,
  country_rank INT NULL,
  total_score BIGINT NULL,
  rank_total_score BIGINT NULL,
  accuracy DOUBLE NULL,
  play_count INT NULL,
  total_hit_count BIGINT NULL,
  total_play_time BIGINT NULL,
  grades JSON NULL,
  PRIMARY KEY (id, mode, subserver, record_date_time),
  KEY idx_record_date_time_mode (record_date_time, mode)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS player_stats_table_meta (
  year SMALLINT NOT NULL,
  table_name VARCHAR(64) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS player_stats_watch (
  id INT NOT NULL,
  mode TINYINT NOT NULL,
  subserver TINYINT NOT NULL,
  active TINYINT(1) NOT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id, mode, subserver)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
