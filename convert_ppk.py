#!/usr/bin/env python3
import base64
import struct
import sys

def ppk_to_openssh(ppk_content):
    lines = ppk_content.strip().split('\n')
    if not lines[0].startswith('PuTTY-User-Key-File-3:'):
        raise ValueError('Not a PuTTY key file')
    
    # Extract private key data
    private_start = None
    for i, line in enumerate(lines):
        if line == 'Private-Lines: 14':
            private_start = i + 1
            break
    
    if private_start is None:
        raise ValueError('Private key not found')
    
    # Decode private key
    private_data = ''.join(lines[private_start:private_start+14])
    private_bytes = base64.b64decode(private_data)
    
    # Extract key components
    pos = 0
    
    # Skip header
    pos += 4
    
    # Read key type
    key_type_len = struct.unpack('>I', private_bytes[pos:pos+4])[0]
    pos += 4
    key_type = private_bytes[pos:pos+key_type_len].decode('ascii')
    pos += key_type_len
    
    # Read n (modulus)
    n_len = struct.unpack('>I', private_bytes[pos:pos+4])[0]
    pos += 4
    n = private_bytes[pos:pos+n_len]
    pos += n_len
    
    # Read e (exponent)
    e_len = struct.unpack('>I', private_bytes[pos:pos+4])[0]
    pos += 4
    e = private_bytes[pos:pos+e_len]
    pos += e_len
    
    # Read d (private exponent)
    d_len = struct.unpack('>I', private_bytes[pos:pos+4])[0]
    pos += 4
    d = private_bytes[pos:pos+d_len]
    pos += d_len
    
    # Read p (prime 1)
    p_len = struct.unpack('>I', private_bytes[pos:pos+4])[0]
    pos += 4
    p = private_bytes[pos:pos+p_len]
    pos += p_len
    
    # Read q (prime 2)
    q_len = struct.unpack('>I', private_bytes[pos:pos+4])[0]
    pos += 4
    q = private_bytes[pos:pos+q_len]
    pos += q_len
    
    # Read iqmp (inverse of q mod p)
    iqmp_len = struct.unpack('>I', private_bytes[pos:pos+4])[0]
    pos += 4
    iqmp = private_bytes[pos:pos+iqmp_len]
    
    # Construct OpenSSH format
    openssh_data = b''
    openssh_data += struct.pack('>I', len(b'ssh-rsa'))
    openssh_data += b'ssh-rsa'
    openssh_data += struct.pack('>I', len(n))
    openssh_data += n
    openssh_data += struct.pack('>I', len(e))
    openssh_data += e
    openssh_data += struct.pack('>I', len(d))
    openssh_data += d
    openssh_data += struct.pack('>I', len(p))
    openssh_data += p
    openssh_data += struct.pack('>I', len(q))
    openssh_data += q
    openssh_data += struct.pack('>I', len(iqmp))
    openssh_data += iqmp
    
    # Encode in base64
    encoded = base64.b64encode(openssh_data).decode('ascii')
    
    # Format as OpenSSH private key
    result = '-----BEGIN RSA PRIVATE KEY-----\n'
    for i in range(0, len(encoded), 64):
        result += encoded[i:i+64] + '\n'
    result += '-----END RSA PRIVATE KEY-----\n'
    
    return result

if __name__ == '__main__':
    try:
        # Read PPK file
        with open('frontend/sftp.ppk', 'r') as f:
            ppk_content = f.read()

        # Convert to OpenSSH format
        openssh_key = ppk_to_openssh(ppk_content)

        # Write to file
        with open('frontend/id_rsa_converted', 'w') as f:
            f.write(openssh_key)

        print('Conversion completed successfully!')
        print('Converted key saved as: frontend/id_rsa_converted')
        
    except Exception as e:
        print(f'Error: {e}')
        sys.exit(1)
