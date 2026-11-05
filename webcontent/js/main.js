/**
 * Student Hostel Management System (SHMS) - Client-side Utilities
 */

document.addEventListener('DOMContentLoaded', () => {
    // Theme toggle initialization (Light / Dark)
    const storedTheme = localStorage.getItem('shms-theme') || 'light';
    document.documentElement.setAttribute('data-theme', storedTheme);

    // Modal dialog controls
    document.querySelectorAll('[data-modal-open]').forEach(button => {
        button.addEventListener('click', () => {
            const modalId = button.getAttribute('data-modal-open');
            const modal = document.getElementById(modalId);
            if (modal) modal.style.display = 'block';
        });
    });

    document.querySelectorAll('[data-modal-close]').forEach(button => {
        button.addEventListener('click', () => {
            const modal = button.closest('.modal-backdrop');
            if (modal) modal.style.display = 'none';
        });
    });

    window.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal-backdrop')) {
            e.target.style.display = 'none';
        }
    });
});

// Modal Population Helpers
function openEditStudentModal(id, name, roll, course, contact, emergency, address) {
    document.getElementById('editStudentId').value = id;
    document.getElementById('editFullName').value = name;
    document.getElementById('editRollNumber').value = roll;
    document.getElementById('editCourse').value = course;
    document.getElementById('editContactNumber').value = contact;
    document.getElementById('editEmergencyContact').value = emergency;
    document.getElementById('editAddress').value = address;
    document.getElementById('editStudentModal').style.display = 'block';
}

function openEditRoomModal(id, number, block, capacity, fee, status) {
    document.getElementById('editRoomId').value = id;
    document.getElementById('editRoomNumber').value = number;
    document.getElementById('editBlockWing').value = block;
    document.getElementById('editCapacity').value = capacity;
    document.getElementById('editMonthlyFee').value = fee;
    document.getElementById('editRoomStatus').value = status;
    document.getElementById('editRoomModal').style.display = 'block';
}

function openRecordPaymentModal(feeId, studentName, due, paid) {
    document.getElementById('paymentFeeId').value = feeId;
    document.getElementById('paymentStudentName').innerText = studentName;
    const balance = Math.max(0, due - paid);
    document.getElementById('paymentAmountDue').innerText = "$" + balance.toFixed(2);
    document.getElementById('paymentAmountPaid').value = balance.toFixed(2);
    document.getElementById('recordPaymentModal').style.display = 'block';
}

function openUpdateComplaintModal(id, studentName, category, status, remarks) {
    document.getElementById('complaintId').value = id;
    document.getElementById('complaintStudent').innerText = studentName + ' (' + category + ')';
    document.getElementById('complaintStatus').value = status;
    document.getElementById('complaintRemarks').value = remarks || '';
    document.getElementById('updateComplaintModal').style.display = 'block';
}
