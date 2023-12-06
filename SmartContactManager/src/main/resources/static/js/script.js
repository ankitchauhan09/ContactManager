//side bar closing button start
const toggleSideBar = () => {
    if ($('.sidebar').is(':visible')) {
        $('.sidebar').css("display", "none");
        $('.content').css("margin-left", "5%");
    } else {
        $('.sidebar').css("display", "block");
        $('.content').css("margin-left", "22%");

    }
}


$('#updateContactDetailsForm').on('submit', (event) => {
    event.preventDefault();

    const contactId = $("#contactData").attr("value");

    const formData = new FormData($('#updateContactDetailsForm')[0]);

    $.ajax({
        type: 'POST',
        url: '/user/contact/' + contactId + '/update-details',
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            // alert('Success: ' + response);
            location.reload();
        },
        error: (response) => {
            alert('Error: ' + response);
        },
    });
});

$('#updateUserDetailsForm').on('submit', (event) => {
    event.preventDefault();

    const contactId = $("#userData").attr("value");

    let form = document.getElementById("forgotPasswordForm");
    const formData = new FormData(form);

    $.ajax({
        type: 'POST',
        url: '/user/update-user',
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            // alert('Success: ' + response);
            location.reload();
        },
        error: (response) => {
            alert('Error: ' + response);
        },
    });
});

$('#deleteContactBtn').on('click', function (event) {
    event.preventDefault();
    let val = $('#getContactId').attr("value");

    fetch("/user/contact/delete-user/" + val)
        .then(response => response.text())
        .then(data => {
                if (data === "success") {
                    location.reload();
                }
            }
        )
        .catch(error => console.error(error));
})


const searchContact = () => {
    let query = $('#search-input').val();
    // console.log(query);
    if (query == '') {
        $('.search-result').hide();
    } else {
        console.log(query);
        $('.search-result').show();
        // $('.search-result').text(query);

        let url = 'http://localhost:8080/search/' + query;
        fetch(url)
            .then((response) => {
                return response.json();
            })
            .then((data) => {
                console.log(data);
                let currentPage = $('.getCurrentPage').attr("value");
                let text = '<div class="list-group">';
                data.forEach((contact) => {
                    text += '<a href="/user/contact/' + contact.id + '/' + currentPage + '">' + contact.name + '</a>'
                })
                text += '</div>';
                $('.search-result').html(text);
            });

    }

}

$('#showHidePassword').on('click', function () {
    let icon = document.getElementById("showHidePassword");
    let passswordField = document.getElementsByClassName("forgotPasswordInput")[0];

    if (passswordField.type == "password") {
        passswordField.type = "text";
        icon.src = "/image/view.png";
    } else {
        passswordField.type = "password";
        icon.src = "/image/hide.png";
    }

})


$('#forgotPasswordForm').on("submit", function (event) {
    event.preventDefault();

    let obj = {
        firstPass: $('#firstPass').val(),
        confPass: $('#confPass').val()
    }

    $.ajax({
        type: "POST",
        url: "/forgot-password/process",
        data: obj,
        success: function (response) {
            if (response === "success") {
                Swal.fire({
                    title: "Password Changed Successfully",
                    text: "Go to the login page to signin with the new password...",
                    icon: "success"
                });
                window.location.href = "localhost:8080/";
            } else {
                Swal.fire({
                    icon: "error",
                    title: "Oops...",
                    text: "Something went wrong!" + response + "",
                });
            }
        },
        error: function (response) {
            Swal.fire({
                icon: "error",
                title: "Oops...",
                text: "Something went wrong! " + response + "",
            });
        }
    })
})

const paymentStarted = () => {
    console.log("payment started")

    let amount = $('#paymentAmount').val();
    console.log(amount)

    if (amount === "" || amount == null) {
        alert("null amount not accepted")
        return;
    }

    $.ajax({
        url: "/user/create/order",
        data: JSON.stringify({amount: amount, info: "order_request"}),
        contentType: "application/json",
        type: "POST",
        dataType: "json",
        success: function (response) {
            console.log(response);

            if (response.status == "created") {
                let options = {
                    key: "rzp_test_gyvRn5N8dED2Dm",
                    amount: response.amount,
                    currency: "INR",
                    name: "Smart Contact Manager",
                    description: "donation",
                    image: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAKgAAAB+CAMAAACH8qq0AAAAbFBMVEX///8TDA0AAADo5+cJAADR0NAQCAn7+/v39/fc29sNAwUEAADk4+NtamsxLi7s7OwnJCS8u7txb28gHR6BgIDFxMQ8OTmmpKRUUVK2tLSQjo54dXZbWVk/Pj6KiIiuq6ycmpoYFhZJRUZlYmJCt8s/AAAD1klEQVR4nO1a6dKiQAyUMJzDpX6K9+fx/u+4eKCSDIJbO8dWpX+Klk1L0j2JkwmDwWAwGAwGg8FgMBgMAyhL2wxG4ufHNoNx8AF82xzGIMoB8sg2ixFYQBjCwjaLYcRL8DxYxrZ5DKJueDZMa9s8hpAV4kpUFJltJgM43ARtJD3YZvIZAaR3oikEtrl8Qrx6CNpIunK5ntZPng3TtW02/fCX8kVULt31p82boI2kG9t8+vCsJMfrKdp2BG0k3bpp+VMIu0RDmNrmpEQlPQRZ2eakwgYwTzfrKbmbfBeiSGzzIpgpBG0kndnmhRHgSmrrybEWFe+UgjaS7tyy/LqHp2sROp4rKulRT3OXJD30CupWhA68tJ9o6jlTT1FfJbX15IrlT4WyNbUIhSOWX+6RoCl6EGDvxtTsiHiK5RL1ADja5nhFgisJplMsseeC5ZO4vJ9M8MMAW9ssbzOxbulc0zLO0A5MzcoVFi+/voyzFKxs11ONYn0It6FTBujBlZYt369wgT8ME+d9Udk95WOTl22kTwoktV3Lx5XkwW976Rfdgt16IpX08qDyTOrJHk+imnxTbSF71TaNAdFwpoKzrRb1g50y7DhlRrzV0qIsEZgIGjfgFpUKK5YfYfeRBWqVnXHp7U5mNiI0jkiKMIcDoJWpWYxnYopaIad9WZk/kmK1PFVDXwyrrhsZnonBWfU27AjmF2U4LofqQ3Fywp3BcIQmJ/m+zEFalNlTfkx+0r7BDa05o4sybPIfRsu06AxaPo3L/ScN0qJMRmgyE/u0TqQtyliEJtNl2H16+4X4k6F6irHJh6eP3xzMcYeYmamnNRF0IL7hFhWaWTzHBamOAbehKaowISlZfA37N5nwm1iUJRV+4oaHIGScklb6I3T+TWtqQbNrrpsnPclfxqR20ih0n/Lp4kusS79F9t6mouD5ul8uPNJ69dYTce6GKTxRdE4a6/nrCt2a6I3QpWLxFT4gYYZMPMtBtFfp/c11nvJxXH5/6ArVUaRS73JvkmqM0D0r5AYStsqGk8ygb/mo0/JxO3yps+/tUb/L3g9pm5oRk39AwOWDiWY7RSndJdVl+f5M+ZVwGsjstacSNSXF9+8Q1YL8E6eRc9AOkwu9QylqnfOdNY5O4NUjOndcC/y5QnPUyzoz0RAuI0s32HUecDhrH0TE+etnBNiMNsJ4A89bTCE3EUiPc/mQc/VVJwzOD1Hl/KiJGsL0BHc5vzRB/3ATFU7Gho9Na/xWzjsWjaiwMzgnK3PY/lWmKLeQm105/LVPO/NPGAaDwWAwGAwGg8FgMP5//AExayec3FHuqAAAAABJRU5ErkJggg==",
                    order_id: response.id,
                    handler: function (response) {
                        console.log(response.razorpay_payment_id)
                        console.log(response.razorpay_order_id)
                        console.log(response.razorpay_signature)
                        console.log("payment successfull!")
                        alert("congrats! payment successfully");
                    },
                    prefill:
                        {
                            name: "",
                            email: "",
                            contact: "",
                        },
                    notes: {
                        address: "Hello my name is Ankit",
                    },
                    theme: {
                        color: "#D5E3FF",
                    },
                }
                let rzp = new Razorpay(options);
                rzp.on("payment.failed", function (response) {
                    console.log(response.error.code);
                    console.log(response.error.description);
                    console.log(response.error.source);
                    console.log(response.error.step);
                    console.log(response.error.reason);
                    console.log(response.error.metadata.order_id);
                    console.log(response.error.metadata.payment_id);
                    alert("Oops payment failed")
                })
                rzp.open();
            }

        }
        , error: function (response) {
            console.log(response)
        }
    })
}

