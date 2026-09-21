INSERT INTO users (
    name,
    email,
    phone,
    cpf,
    date_birth,
    address,
    is_admin,
    is_disabled,
    password,
    created_at,
    updated_at
)
VALUES (
           'Administrador',
           'admin@admin.com',
           '(00) 90000-0000',
           '000.000.000-00',
           '2000-01-01',
           'Administrador',
           TRUE,
           FALSE,
           '$2a$10$eLuHy/etLkGY3kKNcxN7BOvn.iBuAp08P5DNt2MZuLld8RKd4EDFK',
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );