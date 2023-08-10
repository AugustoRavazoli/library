INSERT INTO
	app_user (username, password, role, enabled)
VALUES
	('username', '$2a$10$TKJ6hXTseIzvSC.Zt1MluOtBjyLp7kM9/f1l/kRNBWq2LBxt.PHcK', 'USER', true);
/* The unhashed password is "password" without the quotes */

INSERT INTO
	category (name, owner_id)
VALUES
	(
		'Fantasia',
		(SELECT id FROM app_user WHERE username = 'username')
	),
	(
		'Romance',
		(SELECT id FROM app_user WHERE username = 'username')
	),
  (
    'Fic��o cient�fica',
	  (SELECT id FROM app_user WHERE username = 'username')
  );

INSERT INTO
	book (title, description, category_id, owner_id)
VALUES
	(
		'O Hobbit',
		'Aventuras do Bilbo',
		(SELECT id FROM category WHERE name = 'Fantasia'),
		(SELECT id FROM app_user WHERE username = 'username')
	),
  (
    'O Senhor dos An�is',
    'Aventuras do Frodo',
		(SELECT id FROM category WHERE name = 'Fantasia'),
		(SELECT id FROM app_user WHERE username = 'username')
  ),
  (
    'Guerra dos mundos',
    'Invas�o de ET',
		(SELECT id FROM category WHERE name = 'Fic��o cient�fica'),
		(SELECT id FROM app_user WHERE username = 'username')
  ),
	(
		'Romeu e Julieta', 
		'Jovens din�micos',
		(SELECT id FROM category WHERE name = 'Romance'),
		(SELECT id FROM app_user WHERE username = 'username')
	);
