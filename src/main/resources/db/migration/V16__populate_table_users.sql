INSERT INTO public.users
(
    id,
    created_at,
    updated_at,
    account_non_expired,
    account_non_locked,
    credentials_non_expired,
    enabled,
    login,
    password
)
VALUES
    ('92825d33-b684-485c-a8e7-96c0a2cf0c84', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, true, true, true, 'teste@teste.com', '$2a$10$hNtxQSgV0lAMiXMmHcFOiOGqpYdkWKawuoHyFQ.ZWHS0WjDnGcxGu')