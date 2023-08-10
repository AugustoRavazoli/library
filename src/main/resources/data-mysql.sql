INSERT IGNORE INTO
	app_user (username, password, role, enabled)
VALUES
	('admin', '$2a$10$7D5N.bUM/Cp1OTO0nASDx.qm0v04Tq8H4/zzKkdzJ5U4BD0WAoH26', 'ADMIN', true);
/*The unhashed password is "admin" without the quotes */
