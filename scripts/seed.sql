-- Base extensa de QA para o banco local (leasily_app_db, schema Room versão 1).
--
-- APAGA todos os dados de domínio antes de inserir. Datas são relativas ao dia em que o script roda,
-- então o status das Estadias (aguardando check-in / em andamento / finalizada) bate com o calendário.
--
-- Uso (app parado, Corretor já logado ao menos uma vez para o banco existir):
--   make pull-db DB=qa/seed.db
--   sqlite3 qa/seed.db < scripts/seed.sql
--   make push-db DB=qa/seed.db
--
-- Volume: 15 Proprietários, 40 Imóveis, 60 Clientes (45 com Estadias, 15 só Leads), ~49 Atendimentos,
-- 200 Períodos Ocupados (Estadias + Períodos indisponíveis) com Movimentações financeiras e anexos.
--
-- Datas: o Room grava data/hora "de parede" como epoch millis UTC (AppDateTime.toEpochMillisUTC), por
-- isso o horário local é convertido com strftime('%s', ...) sem ajuste de fuso.
-- Anexos: as URIs são fictícias — a lista mostra nome/ícone, mas abrir o arquivo falha. Para testar
-- abertura, anexe um arquivo real pelo app.

PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;

-- ========================================================
-- Limpeza
-- ========================================================

DELETE FROM stay_transfers;
DELETE FROM stay_files;
DELETE FROM stays;
DELETE FROM assistance_interactions;
DELETE FROM assistance_preferred_neighborhoods;
DELETE FROM assistance_sent_properties;
DELETE FROM assistance_property_characteristics;
DELETE FROM assistances;
DELETE FROM lead_tenants;
DELETE FROM property_prices;
DELETE FROM property_files;
DELETE FROM property_characteristics_properties;
DELETE FROM properties;
DELETE FROM property_owner_files;
DELETE FROM property_owners;
DELETE FROM sqlite_sequence;

-- ========================================================
-- Tabelas auxiliares (só existem durante o script)
-- ========================================================

CREATE TEMP TABLE seed_params AS
SELECT
    10000 AS contract_version,
    datetime('now', 'localtime') AS now_local,
    CAST(strftime('%s', datetime('now', 'localtime')) AS INTEGER) * 1000 AS now_ms,
    CAST(strftime('%Y', 'now', 'localtime') AS INTEGER) AS year;

CREATE TEMP TABLE seed_neighborhoods (i INTEGER PRIMARY KEY, name TEXT, street TEXT, cep_prefix TEXT);
INSERT INTO seed_neighborhoods VALUES
    (0, 'Centro',       'Avenida Brasil',                 '88330'),
    (1, 'Pioneiros',    'Avenida Atlântica',              '88331'),
    (2, 'Barra Sul',    'Rua 4000',                       '88330'),
    (3, 'Praia Brava',  'Avenida José Medeiros Vieira',   '88334'),
    (4, 'Nações',       'Rua Dinamarca',                  '88338'),
    (5, 'Estados',      'Rua Paraná',                     '88339'),
    (6, 'Ariribá',      'Rua Ariribá',                    '88338'),
    (7, 'Praia dos Amores', 'Avenida Rodesindo Pavan',    '88334');

CREATE TEMP TABLE seed_first_names (i INTEGER PRIMARY KEY, name TEXT, slug TEXT);
INSERT INTO seed_first_names VALUES
    (0, 'Ana', 'ana'), (1, 'Bruno', 'bruno'), (2, 'Camila', 'camila'), (3, 'Diego', 'diego'),
    (4, 'Eduarda', 'eduarda'), (5, 'Felipe', 'felipe'), (6, 'Gabriela', 'gabriela'),
    (7, 'Henrique', 'henrique'), (8, 'Isabela', 'isabela'), (9, 'João', 'joao'),
    (10, 'Larissa', 'larissa'), (11, 'Marcelo', 'marcelo'), (12, 'Natália', 'natalia'),
    (13, 'Otávio', 'otavio'), (14, 'Patrícia', 'patricia'), (15, 'Rafael', 'rafael'),
    (16, 'Sofia', 'sofia'), (17, 'Thiago', 'thiago'), (18, 'Valentina', 'valentina'),
    (19, 'Lucas', 'lucas');

CREATE TEMP TABLE seed_last_names (i INTEGER PRIMARY KEY, name TEXT, slug TEXT);
INSERT INTO seed_last_names VALUES
    (0, 'Silva', 'silva'), (1, 'Souza', 'souza'), (2, 'Oliveira', 'oliveira'),
    (3, 'Pereira', 'pereira'), (4, 'Costa', 'costa'), (5, 'Rodrigues', 'rodrigues'),
    (6, 'Almeida', 'almeida'), (7, 'Nascimento', 'nascimento'), (8, 'Lima', 'lima'),
    (9, 'Araújo', 'araujo'), (10, 'Fernandes', 'fernandes'), (11, 'Carvalho', 'carvalho'),
    (12, 'Gomes', 'gomes'), (13, 'Martins', 'martins'), (14, 'Rocha', 'rocha');

CREATE TEMP TABLE seed_cities (i INTEGER PRIMARY KEY, name TEXT);
INSERT INTO seed_cities VALUES
    (0, 'Curitiba'), (1, 'São Paulo'), (2, 'Porto Alegre'), (3, 'Blumenau'), (4, 'Joinville'),
    (5, 'Buenos Aires'), (6, 'Florianópolis'), (7, 'Londrina'), (8, 'Campinas'), (9, 'Montevidéu');

CREATE TEMP TABLE seed_contact_origins (i INTEGER PRIMARY KEY, name TEXT);
INSERT INTO seed_contact_origins VALUES
    (0, 'WhatsApp'), (1, 'Instagram'), (2, 'Indicação'), (3, 'Airbnb'), (4, 'Google'), (5, 'Facebook');

CREATE TEMP TABLE seed_tags (i INTEGER PRIMARY KEY, name TEXT);
INSERT INTO seed_tags VALUES
    (0, 'Família'), (1, 'Casal'), (2, 'Corporativo'), (3, 'Recorrente'), (4, 'VIP'), (5, NULL);

CREATE TEMP TABLE seed_interaction_titles (i INTEGER PRIMARY KEY, title TEXT, description TEXT);
INSERT INTO seed_interaction_titles VALUES
    (0, 'Primeiro contato',          'Lead pediu opções para o período e informou número de hóspedes.'),
    (1, 'Envio de opções',           'Enviadas fotos e valores de 3 Imóveis pelo WhatsApp.'),
    (2, 'Ligação de acompanhamento', 'Lead ainda comparando valores; pediu desconto para pagamento à vista.'),
    (3, 'Visita virtual',            'Chamada de vídeo mostrando o Imóvel e a vista para o mar.'),
    (4, 'Negociação do sinal',       'Combinado sinal de 30% via Pix para garantir a reserva.');

-- Sequência 1..200 reaproveitada pelos INSERTs
CREATE TEMP TABLE seed_seq (n INTEGER PRIMARY KEY);
WITH RECURSIVE s(n) AS (SELECT 1 UNION ALL SELECT n + 1 FROM s WHERE n < 200)
INSERT INTO seed_seq SELECT n FROM s;

-- ========================================================
-- Proprietários
-- ========================================================

INSERT INTO property_owners (id, create_date_time, update_date_time, name, phone, email, payment_key, is_deleted, contract_version)
SELECT
    q.n,
    p.now_ms - (400 - q.n) * 86400000,
    p.now_ms - (400 - q.n) * 86400000,
    f.name || ' ' || l.name,
    CASE WHEN q.n % 7 = 0 THEN NULL ELSE printf('(47) 9%04d-%04d', 8000 + q.n * 13, 1000 + q.n * 271) END,
    CASE WHEN q.n % 5 = 0 THEN NULL ELSE f.slug || '.' || l.slug || '@proprietario.com.br' END,
    CASE q.n % 3
        WHEN 0 THEN printf('%03d.%03d.%03d-%02d', 100 + q.n * 7, 200 + q.n * 11, 300 + q.n * 13, q.n * 3 % 100)
        WHEN 1 THEN f.slug || '.' || l.slug || '@pix.com.br'
        ELSE NULL
    END,
    0,
    p.contract_version
FROM seed_seq q
JOIN seed_params p
JOIN seed_first_names f ON f.i = (q.n * 3 + 1) % 20
JOIN seed_last_names l ON l.i = (q.n * 2 + 5) % 15
WHERE q.n <= 15;

INSERT INTO property_owner_files (property_owner_file_uri, property_owner_file_name, property_owner_id)
SELECT 'content://com.android.providers.downloads.documents/document/' || (1000 + q.n),
       'contrato-administracao-proprietario-' || q.n || '.pdf',
       q.n
FROM seed_seq q WHERE q.n <= 15 AND q.n % 4 != 0
UNION ALL
SELECT 'content://com.android.providers.downloads.documents/document/' || (2000 + q.n),
       'documento-identidade-proprietario-' || q.n || '.jpg',
       q.n
FROM seed_seq q WHERE q.n <= 15 AND q.n % 2 = 0;

-- ========================================================
-- Imóveis
-- ========================================================

INSERT INTO properties (
    id, create_date_time, update_date_time, neighborhood, street_address, cep_postal_code, number,
    address_line_2, parking_spot_number, parking_basement_level_number, hobby_box_number, bedrooms,
    en_suites, double_beds, single_beds, bathrooms, parking_spots, air_conditioners, max_guests,
    distance_to_sea, included_items, has_included_items, cleaning_service_tax,
    has_cleaning_service_included, observations, is_deleted, property_owner_id, property_type_id,
    property_sea_proximity_type_id, contract_version
)
SELECT
    q.n,
    p.now_ms - (380 - q.n * 5) * 86400000,
    p.now_ms - (380 - q.n * 5) * 86400000,
    nb.name,
    nb.street,
    nb.cep_prefix || printf('-%03d', q.n * 17 % 1000),
    10 + q.n * 37 % 1990,
    CASE WHEN q.n % 3 != 0 THEN 'Apto ' || ((q.n % 20 + 1) * 100 + q.n % 4 + 1) ELSE NULL END,
    CASE WHEN q.n % 3 != 0 AND q.n % 4 != 0 THEN CAST(q.n * 3 % 90 + 1 AS TEXT) ELSE NULL END,
    CASE WHEN q.n % 3 != 0 AND q.n % 4 != 0 THEN 'G' || (q.n % 3 + 1) ELSE NULL END,
    CASE WHEN q.n % 6 = 1 THEN CAST(q.n % 30 + 1 AS TEXT) ELSE NULL END,
    q.n % 4 + 1,                                              -- dormitórios
    min(q.n % 4 + 1, q.n % 3),                                -- suítes
    q.n % 4 + 1,                                              -- camas de casal
    (q.n % 3) * 2,                                            -- camas de solteiro
    min(q.n % 4 + 1, q.n % 3) + 1,                            -- banheiros
    q.n % 3,                                                  -- vagas
    q.n % 4 + 1,                                              -- ar-condicionados
    (q.n % 4 + 1) * 2 + (q.n % 3) * 2,                        -- máximo de hóspedes
    CASE WHEN q.n % 4 = 3 THEN CAST(150 + q.n * 25 AS TEXT) ELSE NULL END,
    CASE WHEN q.n % 2 = 0 THEN 'Roupa de cama, toalhas, 2 cadeiras e 1 guarda-sol' ELSE NULL END,
    CASE WHEN q.n % 2 = 0 THEN 1 ELSE 0 END,
    CASE WHEN q.n % 5 = 0 THEN NULL ELSE printf('%.2f', 150 + (q.n % 4) * 40) END,
    CASE WHEN q.n % 5 = 0 THEN 1 ELSE 0 END,
    CASE q.n % 6
        WHEN 0 THEN 'Chaves na portaria; avisar o zelador com 1 dia de antecedência.'
        WHEN 3 THEN 'Proprietário não aceita animais de grande porte.'
        ELSE NULL
    END,
    0,
    (q.n - 1) % 15 + 1,
    CASE WHEN q.n % 3 = 0 THEN 2 ELSE 1 END,                  -- 1 = Apartamento, 2 = Casa
    q.n % 4 + 1,                                              -- 1 frente mar, 2 vista mar, 3 longe, 4 distância
    p.contract_version
FROM seed_seq q
JOIN seed_params p
JOIN seed_neighborhoods nb ON nb.i = q.n % 8
WHERE q.n <= 40;

-- Características: 16 opções; cada Imóvel recebe um subconjunto determinístico
INSERT INTO property_characteristics_properties (property_id, property_characteristic_id)
SELECT pr.id, c.n
FROM properties pr
JOIN seed_seq c ON c.n <= 16
WHERE (pr.id * 7 + c.n * 3) % 4 = 0;

-- Fotos: 1 principal + 2 extras por Imóvel
INSERT INTO property_files (property_file_uri, is_main_picture, property_id)
SELECT 'content://media/external/images/media/' || (pr.id * 10 + k.n),
       CASE WHEN k.n = 1 THEN 1 ELSE 0 END,
       pr.id
FROM properties pr
JOIN seed_seq k ON k.n <= 3;

-- Diárias: alta temporada (jan–fev), baixa (mar–nov) e alta com Réveillon (dez–fev do ano seguinte)
INSERT INTO property_prices (start_date, end_date, amount, property_id)
SELECT
    CAST(strftime('%s', per.start_date) AS INTEGER) * 1000,
    CAST(strftime('%s', per.end_date) AS INTEGER) * 1000,
    printf('%.2f', (250 + pr.bedrooms * 120 + CASE WHEN pr.property_sea_proximity_type_id = 1 THEN 150 ELSE 0 END) * per.factor),
    pr.id
FROM properties pr
JOIN (
    SELECT (p.year || '-01-01') AS start_date, date(p.year || '-03-01', '-1 day') AS end_date, 1.3 AS factor FROM seed_params p
    UNION ALL
    SELECT (p.year || '-03-01'), (p.year || '-11-30'), 1.0 FROM seed_params p
    UNION ALL
    SELECT (p.year || '-12-01'), date((p.year + 1) || '-03-01', '-1 day'), 1.6 FROM seed_params p
) per;

-- ========================================================
-- Clientes (1–45 com Estadias; 46–60 só Leads)
-- ========================================================

INSERT INTO lead_tenants (
    id, create_date_time, update_date_time, tag, name, city, identity_document, phone, email,
    contact_origin, observations, is_deleted, contract_version
)
SELECT
    q.n,
    p.now_ms - (300 - q.n * 4) * 86400000,
    p.now_ms - (300 - q.n * 4) * 86400000,
    t.name,
    f.name || ' ' || l.name,
    CASE WHEN q.n % 9 = 0 THEN NULL ELSE c.name END,
    CASE WHEN q.n % 4 = 0 THEN NULL
         ELSE printf('%03d.%03d.%03d-%02d', q.n * 13 % 1000, q.n * 29 % 1000, q.n * 31 % 1000, q.n * 7 % 100) END,
    printf('(%02d) 9%04d-%04d', 41 + q.n % 8, 7000 + q.n * 17 % 3000, 1000 + q.n * 113 % 9000),
    CASE WHEN q.n % 3 = 0 THEN NULL ELSE f.slug || '.' || l.slug || q.n || '@email.com' END,
    CASE WHEN q.n % 10 = 0 THEN NULL ELSE o.name END,
    CASE q.n % 8
        WHEN 0 THEN 'Prefere andares altos e Imóveis com elevador.'
        WHEN 4 THEN 'Viaja com cachorro de pequeno porte.'
        ELSE NULL
    END,
    0,
    p.contract_version
FROM seed_seq q
JOIN seed_params p
JOIN seed_first_names f ON f.i = q.n % 20
JOIN seed_last_names l ON l.i = (q.n * 7 + q.n / 20) % 15
JOIN seed_cities c ON c.i = q.n % 10
JOIN seed_contact_origins o ON o.i = q.n % 6
JOIN seed_tags t ON t.i = q.n % 6
WHERE q.n <= 60;

-- ========================================================
-- Períodos Ocupados (200 = 40 Imóveis × 5 janelas)
-- ========================================================
-- Janelas relativas a hoje (dias): -100, -40, -6, +20, +50, deslocadas até 7 dias por Imóvel e com
-- 3–10 diárias, sem sobreposição no mesmo Imóvel. A janela -6 gera Estadias em andamento e
-- recém-finalizadas; as futuras ficam aguardando check-in. 1 a cada 11 é Período indisponível.

CREATE TEMP TABLE seed_stays AS
SELECT
    k.n AS id,
    (k.n - 1) % 40 + 1 AS property_id,
    CASE (k.n - 1) / 40 WHEN 0 THEN -100 WHEN 1 THEN -40 WHEN 2 THEN -6 WHEN 3 THEN 20 ELSE 50 END
        + ((k.n - 1) % 40) % 8 AS start_offset,
    3 + k.n % 8 AS nights,
    k.n % 11 = 0 AS is_unavailable
FROM seed_seq k;

INSERT INTO stays (
    id, create_date_time, update_date_time, vehicle_make, vehicle_color, vehicle_license_plate, guests,
    check_in_date_time, check_out_date_time, is_auto_check_in, is_auto_check_out, total_price,
    commission, observations, is_deleted, status, property_id, tenant_id, contract_version
)
SELECT
    s.id,
    ci.ms - 30 * 86400000,
    ci.ms - 30 * 86400000,
    CASE WHEN NOT s.is_unavailable AND s.id % 3 = 0 THEN
        CASE s.id % 4 WHEN 0 THEN 'Volkswagen T-Cross' WHEN 1 THEN 'Fiat Toro' WHEN 2 THEN 'Jeep Compass' ELSE 'Toyota Corolla' END
    END,
    CASE WHEN NOT s.is_unavailable AND s.id % 3 = 0 THEN
        CASE s.id % 3 WHEN 0 THEN 'Branco' WHEN 1 THEN 'Prata' ELSE 'Preto' END
    END,
    CASE WHEN NOT s.is_unavailable AND s.id % 3 = 0 THEN
        printf('%s%s%s%d%s%02d', char(65 + s.id % 26), char(65 + s.id * 3 % 26), char(65 + s.id * 7 % 26), s.id % 10, char(65 + s.id * 5 % 26), s.id * 11 % 100)
    END,
    CASE WHEN s.is_unavailable THEN NULL ELSE min(pr.max_guests, 1 + s.id % 6) END,
    ci.ms,
    co.ms,
    CASE WHEN s.is_unavailable THEN NULL ELSE 0 END,
    CASE WHEN s.is_unavailable THEN NULL ELSE 0 END,
    CASE WHEN s.is_unavailable THEN NULL ELSE printf('%.2f', price.total) END,
    CASE WHEN s.is_unavailable THEN NULL ELSE printf('%.2f', round(price.total * 0.1, 2)) END,
    CASE
        WHEN s.is_unavailable AND s.id % 2 = 0 THEN 'Uso do Proprietário'
        WHEN s.is_unavailable THEN 'Manutenção do ar-condicionado e pintura'
        WHEN s.id % 7 = 0 THEN 'Chegada prevista após as 20h; deixar chave com o porteiro.'
    END,
    0,
    CASE
        WHEN s.is_unavailable THEN NULL
        WHEN co.local <= p.now_local THEN 2                   -- Finalizada
        WHEN ci.local <= p.now_local THEN 1                   -- Em andamento
        ELSE 0                                                -- Aguardando check-in
    END,
    s.property_id,
    -- Janelas atuais/futuras só para Clientes 1–30: os 31–45 ficam só com histórico (Inquilinos antigos)
    CASE
        WHEN s.is_unavailable THEN NULL
        WHEN s.start_offset < -20 THEN (s.id * 7) % 45 + 1
        ELSE (s.id * 7) % 30 + 1
    END,
    p.contract_version
FROM seed_stays s
JOIN seed_params p
JOIN properties pr ON pr.id = s.property_id
JOIN (SELECT id, local, CAST(strftime('%s', local) AS INTEGER) * 1000 AS ms FROM (
    SELECT id, datetime(date('now', 'localtime', printf('%+d days', start_offset)), '+14 hours') AS local FROM seed_stays
)) ci ON ci.id = s.id
JOIN (SELECT id, local, CAST(strftime('%s', local) AS INTEGER) * 1000 AS ms FROM (
    SELECT id, datetime(date('now', 'localtime', printf('%+d days', start_offset + nights)), '+11 hours') AS local FROM seed_stays
)) co ON co.id = s.id
JOIN (
    SELECT s2.id,
           s2.nights * (250 + pr2.bedrooms * 120 + CASE WHEN pr2.property_sea_proximity_type_id = 1 THEN 150 ELSE 0 END)
               + CASE WHEN pr2.has_cleaning_service_included = 1 THEN 0 ELSE 150 + (pr2.id % 4) * 40 END AS total
    FROM seed_stays s2 JOIN properties pr2 ON pr2.id = s2.property_id
) price ON price.id = s.id;

-- Movimentações financeiras: sinal de 30% duas semanas antes; saldo no check-in se já entrou
INSERT INTO stay_transfers (transfer_date, amount, description, stay_id)
SELECT st.check_in_date_time - 14 * 86400000,
       printf('%.2f', round(CAST(st.total_price AS REAL) * 0.3, 2)),
       'Sinal via Pix',
       st.id
FROM stays st WHERE st.tenant_id IS NOT NULL
UNION ALL
SELECT st.check_in_date_time,
       printf('%.2f', CAST(st.total_price AS REAL) - round(CAST(st.total_price AS REAL) * 0.3, 2)),
       CASE WHEN st.id % 2 = 0 THEN 'Saldo no check-in (Pix)' ELSE 'Saldo no check-in (dinheiro)' END,
       st.id
FROM stays st WHERE st.tenant_id IS NOT NULL AND st.status >= 1;

-- Anexos de Estadia: contrato em 1 a cada 3, comprovante de sinal em 1 a cada 5
INSERT INTO stay_files (stay_file_uri, stay_file_name, stay_id)
SELECT 'content://com.android.providers.downloads.documents/document/' || (5000 + st.id),
       'contrato-estadia-' || st.id || '.pdf',
       st.id
FROM stays st WHERE st.tenant_id IS NOT NULL AND st.id % 3 = 0
UNION ALL
SELECT 'content://media/external/images/media/' || (9000 + st.id),
       'comprovante-sinal-' || st.id || '.jpg',
       st.id
FROM stays st WHERE st.tenant_id IS NOT NULL AND st.id % 5 = 0;

-- ========================================================
-- Atendimentos
-- ========================================================
-- Clientes com Estadia: Atendimento Finalizado (conversão), exceto 1 a cada 5, que voltou a procurar
-- (Inquilino/Inquilino antigo com Atendimento Não finalizado). Leads 46–60: todos Não finalizados.
-- Clientes múltiplos de 3 até 45 ficam sem Atendimento.

INSERT INTO assistances (
    id, create_date_time, update_date_time, searched_start_date, searched_end_date,
    has_flexibility_for_period, max_accepted_nightly_rate, adults, kids, babies, animals, bedrooms,
    en_suites, double_beds, single_beds, bathrooms, parking_spots, distance_to_sea, observations,
    is_finished, is_deleted, property_sea_proximity_type_id, lead_tenant_id, contract_version
)
SELECT
    c.id,
    p.now_ms - (60 - c.id) * 86400000,
    p.now_ms - (60 - c.id) * 86400000,
    CAST(strftime('%s', date('now', 'localtime', printf('+%d days', 15 + c.id % 60))) AS INTEGER) * 1000,
    CAST(strftime('%s', date('now', 'localtime', printf('+%d days', 22 + c.id % 60))) AS INTEGER) * 1000,
    c.id % 2,
    CASE WHEN c.id % 4 = 0 THEN NULL ELSE printf('%.2f', 400 + (c.id % 6) * 100) END,
    1 + c.id % 4,
    c.id % 3,
    CASE WHEN c.id % 7 = 0 THEN 1 ELSE 0 END,
    CASE WHEN c.id % 8 = 4 THEN 1 ELSE 0 END,
    1 + c.id % 3,
    c.id % 2,
    1 + c.id % 3,
    c.id % 3,
    1 + c.id % 2,
    c.id % 2,
    CASE WHEN c.id % 4 = 3 THEN CAST(200 + c.id * 10 AS TEXT) ELSE NULL END,
    CASE WHEN c.id % 5 = 1 THEN 'Quer Imóvel perto de restaurantes e com piscina.' ELSE NULL END,
    CASE WHEN c.id <= 45 AND c.id % 5 != 0 THEN 1 ELSE 0 END,
    0,
    CASE WHEN c.id % 5 = 2 THEN NULL ELSE c.id % 4 + 1 END,
    c.id,
    p.contract_version
FROM lead_tenants c
JOIN seed_params p
WHERE c.id > 45 OR c.id % 3 != 0;

INSERT INTO assistance_interactions (date, title, description, assistance_id)
SELECT a.create_date_time + (k.n - 1) * 2 * 86400000 + 10 * 3600000,
       it.title,
       CASE WHEN k.n = 3 AND a.id % 2 = 0 THEN NULL ELSE it.description END,
       a.id
FROM assistances a
JOIN seed_seq k ON k.n <= 1 + a.id % 4
JOIN seed_interaction_titles it ON it.i = k.n - 1;

INSERT INTO assistance_sent_properties (assistance_id, property_id)
SELECT a.id, (a.id * 3 + k.n) % 40 + 1
FROM assistances a
JOIN seed_seq k ON k.n <= 1 + a.id % 3;

INSERT INTO assistance_property_characteristics (assistance_id, property_characteristic_id)
SELECT a.id, (a.id * 5 + k.n * 4) % 16 + 1
FROM assistances a
JOIN seed_seq k ON k.n <= a.id % 4;

-- O bairro é chave primária da tabela: cada bairro pertence a um único Atendimento
INSERT INTO assistance_preferred_neighborhoods (neighborhood, assistance_id)
SELECT nb.name, 46 + nb.i
FROM seed_neighborhoods nb;

COMMIT;

-- ========================================================
-- Resumo
-- ========================================================

SELECT 'Proprietários', COUNT(*) FROM property_owners
UNION ALL SELECT 'Imóveis', COUNT(*) FROM properties
UNION ALL SELECT 'Clientes', COUNT(*) FROM lead_tenants
UNION ALL SELECT 'Atendimentos (não finalizados)', COUNT(*) FROM assistances WHERE is_finished = 0
UNION ALL SELECT 'Atendimentos (finalizados)', COUNT(*) FROM assistances WHERE is_finished = 1
UNION ALL SELECT 'Estadias aguardando check-in', COUNT(*) FROM stays WHERE status = 0
UNION ALL SELECT 'Estadias em andamento', COUNT(*) FROM stays WHERE status = 1
UNION ALL SELECT 'Estadias finalizadas', COUNT(*) FROM stays WHERE status = 2
UNION ALL SELECT 'Períodos indisponíveis', COUNT(*) FROM stays WHERE tenant_id IS NULL
UNION ALL SELECT 'Movimentações financeiras', COUNT(*) FROM stay_transfers
UNION ALL SELECT 'Anexos (Proprietários + Estadias)', (SELECT COUNT(*) FROM property_owner_files) + (SELECT COUNT(*) FROM stay_files);
