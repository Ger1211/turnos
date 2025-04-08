INSERT INTO usuario (email, password) VALUES
('cliente@email.com', '$2a$10$H1Vfl2e4idX1TB4VBbMSJun0a7ZoT.effKjl0z0xhmlzvhhFgm3nm'),
('pro@email.com', '$2a$10$DZJg6r5BFMYI1c2sjfWnDuvhPamGgDR2jlaOyVv4Ws4n6CH0fZWVq'),
('admin@email.com', '$2a$10$cjFKFBoSP8eRUVEU1ikRjuCJlPwmMPnxSMJIGnigbVK55jSls1K9y');

INSERT INTO usuario_roles (usuario_id, roles) VALUES
(1, 'CLIENTE'),
(2, 'PROFESIONAL'),
(3, 'ADMIN');

INSERT INTO cliente (nombre, usuario_id) VALUES
('Cliente Uno', 1);

INSERT INTO profesional (nombre, especialidad, usuario_id) VALUES
('Profesional Uno', 'Manicuría', 2);
