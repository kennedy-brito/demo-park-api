insert into tb_users (id, username, password, role)
    values
        (100, 'ana@gmail.com',  '$2a$12$uB64GhrQ63Zhx6pT6C/VzOrij/W2cMGRpnLb7vttXI2MBd35h9QW2', 'ROLE_ADMIN'),
        (101, 'bia@gmail.com',  '$2a$12$uB64GhrQ63Zhx6pT6C/VzOrij/W2cMGRpnLb7vttXI2MBd35h9QW2', 'ROLE_CLIENT'),
        (102, 'bob@gmail.com',  '$2a$12$uB64GhrQ63Zhx6pT6C/VzOrij/W2cMGRpnLb7vttXI2MBd35h9QW2', 'ROLE_CLIENT'),
        (103, 'toby@gmail.com', '$2a$12$uB64GhrQ63Zhx6pT6C/VzOrij/W2cMGRpnLb7vttXI2MBd35h9QW2', 'ROLE_CLIENT');


insert into tb_clients (id, name, cpf, id_user)
    values
        (10, 'Bianca Silva', '02268984079', 101),
        (20, 'Roberto Gomes', '09684144008', 102);

