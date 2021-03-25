var countDownDate = new Date("Feb 29, 2020 23:59:59").getTime();

if(document.getElementById("countdown")) {
    var x = setInterval(function() {

        var now = new Date().getTime();
        var distance = countDownDate - now;

        var days = Math.floor(distance / (1000 * 60 * 60 * 24));
        var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        var seconds = Math.floor((distance % (1000 * 60)) / 1000);

        document.getElementById("countdown").innerHTML = days + "dni " + hours + "godz "
            + minutes + "min " + seconds + "sek ";

        if (distance < 0) {
            clearInterval(x);
            document.getElementById("countdown").innerHTML = "Oferta Wygasła";
        }
    }, 1000);
}