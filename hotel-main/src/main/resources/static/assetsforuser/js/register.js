$(document).ready(function () {
    $('#email').blur(function () {
        var email = $(this).val();
        var emailField = $(this);
        var emailError = $('#email-error');
        var invalidFeedback = emailField.next('.invalid-feedback');

        $.ajax({
            type: 'POST',
            url: '/user/api/checkEmail', // 필요한 경우 URL을 조정하세요.
            data: { email: email },
            success: function (exists) {
                if (exists) {
                    emailField.addClass('is-invalid');
                    emailError.show();
                    invalidFeedback.hide(); // 중복된 이메일일 경우 유효성 검사 메시지를 숨김
                    emailField[0].setCustomValidity('중복된 이메일입니다.');
                } else {
                    emailError.hide();
                    emailField[0].setCustomValidity('');
                    if (!emailField[0].checkValidity()) {
                        emailField.addClass('is-invalid');
                        invalidFeedback.show(); // 유효성 검사에 실패한 경우 메시지를 표시
                    } else {
                        emailField.removeClass('is-invalid');
                        invalidFeedback.hide();
                    }
                }
            }
        });
    });
});