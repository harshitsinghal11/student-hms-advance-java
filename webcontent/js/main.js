/**
 * Student Hostel Management System (SHMS) - Client-side Utilities
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Theme Toggle Initialization & Handler
    const storedTheme = localStorage.getItem('shms-theme') || 'light';
    document.documentElement.setAttribute('data-theme', storedTheme);

    const themeToggleBtn = document.getElementById('themeToggle');
    if (themeToggleBtn) {
        themeToggleBtn.addEventListener('click', () => {
            const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
            const nextTheme = currentTheme === 'dark' ? 'light' : 'dark';
            document.documentElement.setAttribute('data-theme', nextTheme);
            localStorage.setItem('shms-theme', nextTheme);
        });
    }

    // 2. Mobile Sidebar Navigation Toggle
    const sidebarToggleBtn = document.getElementById('sidebarToggle');
    const sidebar = document.querySelector('.sidebar');
    if (sidebarToggleBtn && sidebar) {
        sidebarToggleBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            sidebar.classList.toggle('sidebar-open');
        });

        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', (e) => {
            if (sidebar.classList.contains('sidebar-open') && !sidebar.contains(e.target) && e.target !== sidebarToggleBtn) {
                sidebar.classList.remove('sidebar-open');
            }
        });
    }

    // 3. Modal Dialog Controls
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
