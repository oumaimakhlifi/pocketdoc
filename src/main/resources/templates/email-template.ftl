<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Renouvellement de convention avec PocketDoc</title>
    <style>
        /* Styles du cadre */
        .cadre2 {
            padding: 10px;
            border: 2px solid #007bff;
            border-radius: 5px;
            background-color: #007bff;
            width: fit-content;
            margin: 20px auto;
            text-align: center;
            color: #fff;
        }
        .cadre2 a {
            color: white;
            text-decoration: none;
        }
        .cadre2 a:hover {
            text-decoration: underline;
        }

        .cadre {
            padding: 20px;
            border: 2px solid #007bff;
            border-radius: 10px;
            background-color: #f2f2f2;
            width: fit-content;
            margin: 20px auto;
            text-align: center;
            color: #333;
            font-family: Arial, sans-serif;
        }
        .cadre a {
            color: #007bff;
            text-decoration: none;
            font-weight: bold;
        }
        .cadre a:hover {
            text-decoration: underline;
        }
        .options {
            margin-top: 20px;
            font-size: 16px;
        }
        .thank-you {
            margin-top: 20px;
            font-style: italic;
        }
        .signature {
            margin-top: 20px;
            font-weight: bold;
        }
    </style>
</head>
<body>

<!-- Cadre HTML avec contenu -->
<div class="cadre">
    <h2 style="color: #007bff;">Renouvellement de convention avec PocketDoc</h2>
    <p>Bonjour Cher/Chère Docteur ${doctorName}, </p>
    <p>Nous espérons que ce message vous trouve bien.</p>
    <p>Nous tenons à vous informer que votre convention actuelle avec PocketDoc est arrivée à expiration. Si vous ne cliquez pas sur le bouton ci-dessous, vous serez redirigé vers nos procédures de renouvellement sur notre plateforme. Si vous souhaitez renouveler automatiquement votre engagement d'un an, veuillez cliquer sur le bouton :</p>
    <div class="cadre2">
        <a href="http://localhost:4200/RENEWedConvention" style="color: white;">Renouveller automatiquement</a>  🔄
    </div>
    <p>Si vous préférez passer par les procédures de demande sur notre plateforme, veuillez accéder à <a href="http://localhost:4200/login">notre plateforme de demande</a>.</p>
    <p>Veuillez nous faire part de votre décision avant le ${expirationDate}.</p>
    <p class="thank-you">Nous vous remercions pour votre collaboration continue.</p>
    <p class="signature">Cordialement,<br>Khlifi Oumaima<br>Responsable de conventions<br>PocketDoc</p>
</div>

</body>
</html>
